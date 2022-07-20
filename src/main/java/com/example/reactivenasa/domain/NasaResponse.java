package com.example.reactivenasa.domain;

import lombok.Data;

import java.util.List;

@Data
public class NasaResponse {
    private List<Photo> photos;
}
