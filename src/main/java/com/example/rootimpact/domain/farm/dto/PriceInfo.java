package com.example.rootimpact.domain.farm.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfo {
    // 날짜별 가격 정보 저장
    private Map<String, Double> pricesByDate = new HashMap<>();

    // 특정 날짜의 가격 정보 추가
    public void addPrice(String date, double price) {
        pricesByDate.put(date, price);
    }




}