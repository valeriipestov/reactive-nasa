package com.example.reactivenasa.service;

import com.example.reactivenasa.domain.NasaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class PictureService {

    @Value("${nasa.api.url}")
    private String nasaApiUrl;
    @Value("${nasa.api.key}")
    private String nasaApiKey;

    public Mono<byte[]> getLargestPicture(int sol) {
        return WebClient.create(nasaApiUrl)
                .get()
                .uri(builder -> builder.
                        queryParam("api_key", nasaApiKey)
                        .queryParam("sol", sol)
                        .build()
                )
                .exchangeToMono(resp -> resp.bodyToMono(NasaResponse.class))
                .map(NasaResponse::getPhotos)
                .flatMapMany(Flux::fromIterable)
                .flatMap(photo ->
                        WebClient.create(photo.getImgSrc())
                                .head()
                                .exchangeToMono(ClientResponse::toBodilessEntity)
                                .mapNotNull(resp -> resp.getHeaders().getLocation())
                                .flatMap(redirectUri ->
                                        WebClient.create(redirectUri.toString())
                                                .mutate()
                                                .codecs(conf -> conf.defaultCodecs().maxInMemorySize(10_000_000))
                                                .build()
                                                .get()
                                                .exchangeToMono(resp -> resp.toEntity(byte[].class))
                                )
                ).reduce((v1, v2) -> v1.getHeaders().getContentLength() > v2.getHeaders().getContentLength() ? v1 : v2)
                .mapNotNull(HttpEntity::getBody);
    }
}
