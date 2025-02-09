package com.example.rootimpact.domain.farm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CropInfo {
    String categoryCode; // 부류코드
    String itemCode; // 품목코드
    String kindCode; // 품종코드
}
