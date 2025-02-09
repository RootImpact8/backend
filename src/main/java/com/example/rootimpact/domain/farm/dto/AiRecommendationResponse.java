package com.example.rootimpact.domain.farm.dto;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AiRecommendationResponse {
    private String cropStage;  //  작물 재배 일차 정보
    private String answer;
}
