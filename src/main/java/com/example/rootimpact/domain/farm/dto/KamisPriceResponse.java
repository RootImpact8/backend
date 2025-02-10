package com.example.rootimpact.domain.farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KamisPriceResponse {
    private String itemName; // 작물명
    private String previousDate; // 이전일: localdate - 2
    private Double previousPrice; // 이전일 가격
    private String currentDate; // 현재일 localdate - 1
    private Double currentPrice; // 현재일 가격
    private Double changeRate; // 변동률(%)
    private String priceStatus; // 가격 상태(상승/하락/동일)
}