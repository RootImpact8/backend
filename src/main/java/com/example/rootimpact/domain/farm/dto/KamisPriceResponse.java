package com.example.rootimpact.domain.farm.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KamisPriceResponse {
    private String itemName; // 작물명
    private List<RegionPriceInfo> regionPrices; // 지역별 가격 정보 목록
    private String startDate; // 조회 시작일
    private String endDate; // 조회 종료일
}