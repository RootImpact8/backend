package com.example.rootimpact.domain.farm.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String city; // 도시명
    private String state; // 도/(광역)시
    private String country; //국가
}
