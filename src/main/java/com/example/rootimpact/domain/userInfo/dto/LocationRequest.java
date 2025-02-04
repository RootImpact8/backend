package com.example.rootimpact.domain.userInfo.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String city;    // 도시명
    private String state;   // 도/시
    private String country; // 국가명
}
