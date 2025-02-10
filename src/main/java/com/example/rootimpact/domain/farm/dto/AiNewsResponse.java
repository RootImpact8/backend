package com.example.rootimpact.domain.farm.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "관심작물 정보 dto 응답")
public class AiNewsResponse {
    @Schema(description = "ai반환한응답",example = "감자는이래이래 정보가좋다")
    private String news; // AI가 반환한 뉴스 내용
}
