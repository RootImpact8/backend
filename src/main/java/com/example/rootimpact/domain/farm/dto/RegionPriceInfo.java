package com.example.rootimpact.domain.farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionPriceInfo{
    private String countyName; // 지역명
    private String regDay; // 조사일(종료일)
    private Double startDayPrice; // 시작일 가격
    private Double endDayPrice; // 종료일 가격
    private Double changeRate; // 변동률(%)
    private String priceStatus; // 가격 상태(상승/하락/동일)
}
