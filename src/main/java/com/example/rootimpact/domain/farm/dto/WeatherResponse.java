package com.example.rootimpact.domain.farm.dto;

import lombok.Data;

@Data
public class WeatherResponse {
    private String description; // 날씨 설명
    private double temperature; // 기온
    private double humidity;    // 습도
}
