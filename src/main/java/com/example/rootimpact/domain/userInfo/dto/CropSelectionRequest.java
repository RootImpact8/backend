package com.example.rootimpact.domain.userInfo.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class CropSelectionRequest {
    private Long userId; // 사용자 ID
    // ✅ 올바른 getter 추가
    @Getter
    private List<String> cultivatedCrops; // 선택한 작물 리스트
    private List<String> interestCrops; // 관심 작물 리스트

}
