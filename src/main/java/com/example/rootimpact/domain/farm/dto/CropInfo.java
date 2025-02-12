package com.example.rootimpact.domain.farm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "작물 정보")
public class CropInfo {
    @Schema(description = "부류코드", example = "100")
    String categoryCode;

    @Schema(description = "품목코드", example = "111")
    String itemCode;

    @Schema(description = "품종코드", example = "01")
    String kindCode;

    @Schema(description = "실제 거래 품목명 (벼->쌀)", example = "쌀")
    String itemName;

    // itemName이 없는 경우 기본 생성자
    public CropInfo(String categoryCode, String itemCode, String kindCode) {
        this.categoryCode = categoryCode;
        this.itemCode = itemCode;
        this.kindCode = kindCode;
        this.itemName = null;
    }
}
