package com.example.rootimpact.domain.userInfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Schema(description = "사용자 작물 선택 요청 DTO")
public class CropSelectionRequest {

    @Schema(description = "사용자 ID", example = "1", required = true)
    private Long userId;

    @Getter
    @Schema(description = "선택한 재배 작물 리스트", example = "[\"감자\", \"딸기\"]", required = true)
    private List<String> cultivatedCrops;

    @Getter
    @Schema(description = "선택한 관심 작물 리스트", example = "[\"상추\", \"고추\"]")
    private List<String> interestCrops;

    // ✅ 최소 한 개의 리스트가 비어있어도 동작할 수 있도록 유효성 검사 추가
    public boolean isValid() {
        return (cultivatedCrops != null && !cultivatedCrops.isEmpty()) ||
                (interestCrops != null && !interestCrops.isEmpty());
    }
}