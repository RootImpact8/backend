package com.example.rootimpact.domain.farm.dto;

import lombok.Data;

@Data
public class AiRecommendationRequest {
    private String city;       // 사용자가 위치한 도시
    private String cropName;   // 작물 이름
    private String status;     // 작물 상태
}
