package com.example.rootimpact.domain.farm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "품종 정보 응답 DTO")
public class RdaVarietyResponse {

    @Schema(description = "작물명", example = "감자")
    private String cropName;

    @Schema(description = "품종명", example = "하이칩")
    private String varietyName;

    @Schema(description = "주요 특성")
    private String mainCharInfo;

}
