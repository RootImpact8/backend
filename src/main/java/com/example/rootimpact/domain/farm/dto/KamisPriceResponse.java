package com.example.rootimpact.domain.farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KamisPriceResponse {
    private String cropName;        // 작물명
    private String wholesaleMarketName; // 도매시장명
    private String saleDate;        // 경락일자
    private String price;           // 경락가
}