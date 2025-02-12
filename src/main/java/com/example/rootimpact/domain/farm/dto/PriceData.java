package com.example.rootimpact.domain.farm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "날짜별 가격 정보")
public class PriceData {
    @Schema(description = "가격 조사 날짜", example = "2025-02-12")
    private String date;

    @Schema(description = "해당 날짜의 가격(원)", example = "3000")
    private int price;

}
