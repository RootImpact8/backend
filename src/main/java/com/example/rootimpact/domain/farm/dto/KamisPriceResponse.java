package com.example.rootimpact.domain.farm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "농산물 가격 정보 응답 DTO")
public class KamisPriceResponse {

    @Schema(description = "작물명", example = "감자")
    private String itemName;

    @Schema(description = "이전일 (기준: localdate - 2)", example = "2025-02-08")
    private String previousDate;

    @Schema(description = "이전일 가격 (원/kg)", example = "2500")
    private Double previousPrice;

    @Schema(description = "현재일 (기준: localdate - 1)", example = "2025-02-09")
    private String currentDate;

    @Schema(description = "현재일 가격 (원/kg)", example = "2700")
    private Double currentPrice;

    @Schema(description = "변동률 (%)", example = "8.0")
    private Double changeRate;

    @Schema(description = "가격 상태 (상승/하락/동일)", example = "상승")
    private String priceStatus;
}