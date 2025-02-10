package com.example.rootimpact.domain.userInfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "사용자 거주 지역 요청 DTO")
public class LocationRequest {

    @Schema(description = "도시명", example = "광명로/강남구", required = true)
    private String city;

    @Schema(description = "도/시", example = "경기도/서울특별시", required = true)
    private String state;

    @Schema(description = "국가명", example = "South Korea", required = true)
    private String country;
}