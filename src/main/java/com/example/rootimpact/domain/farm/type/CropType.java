package com.example.rootimpact.domain.farm.type;

import com.example.rootimpact.domain.farm.dto.CropInfo;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CropType {
    STRAWBERRY(1L, new CropInfo("200", "226", "00","딸기")),
    RICE(2L, new CropInfo("100", "111", "01", "쌀")),
    POTATO(3L, new CropInfo("100", "152", "01","감자")),
    LETTUCE(4L, new CropInfo("200", "214", "01","상추")),
    APPLE(5L, new CropInfo("400", "411", "05","사과")),
    PEPPER(6L, new CropInfo("200", "242", "00","고추"));

    private final Long id; // 작물 ID
    private final CropInfo cropInfo; // 작물 코드 정보(부류코드, 품목코드, 품종코드)

    CropType(Long id, CropInfo cropInfo) {
        this.id = id;
        this.cropInfo = cropInfo;
    }

    public static CropInfo getInfoById(Long id) {
        return Arrays.stream(values())
                .filter(crop -> crop.getId().equals(id))
                .findFirst()
                .map(CropType::getCropInfo)
                .orElseThrow(() -> new RuntimeException("Invalid crop id: " + id));
    }

}
