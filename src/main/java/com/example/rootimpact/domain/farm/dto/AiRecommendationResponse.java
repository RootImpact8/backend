package com.example.rootimpact.domain.farm.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "ai가 날씨,거주지,작물,일기에 응답 dto")
public class AiRecommendationResponse {
    @Schema(description = "일기마지막데이터를 조회한 일차 응답",example = "7일차")
    private String cropStage;  //  작물 재배 일차 정보
    @Schema(description = "ai가 일기,날씨 정보에 대한 응답",example = "감자를 3일째심고잇는주잉고 이렇게하는게좋다")
    private String answer;
}
