package com.example.reactivenasa.endpoint;

import com.example.reactivenasa.service.PictureService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class NasaEndpoint {

    private final PictureService pictureService;

    public NasaEndpoint(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @GetMapping(value = "/pictures/{sol}/largest", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<byte[]> getLargestPicture(@PathVariable int sol) {
        return pictureService.getLargestPicture(sol);
    }
}
