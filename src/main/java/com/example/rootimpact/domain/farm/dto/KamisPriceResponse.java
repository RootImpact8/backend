package com.example.rootimpact.domain.farm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "농산물 가격 정보 응답 DTO")
public class KamisPriceResponse {

    @Schema(description = "작물명", example = "감자")
    private String cropName;

    @Schema(description = "날짜별 가격 정보 리스트")
    private List<PriceData> priceData;

}