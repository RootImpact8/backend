package com.example.rootimpact.domain.farm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiRecommendationResponse {
    private String answer; // AI 응답 메시지
}
