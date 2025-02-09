package com.example.rootimpact.domain.farm.type;

import com.example.rootimpact.domain.farm.dto.CropInfo;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CropType {
    STRAWBERRY("딸기", new CropInfo("200", "226", "00")),
    RICE("쌀", new CropInfo("100", "111", "01")),
    POTATO("감자", new CropInfo("100", "152", "00")),
    LETTUCE("상추", new CropInfo("200", "214", "01")),
    APPLE("사과", new CropInfo("400", "411", "05")),
    PEPPER("고추", new CropInfo("200", "242", "00"));

    private final String name; // 작물명
    private final CropInfo cropInfo; // 작물 코드 정보(부류코드, 품목코드, 품종코드)

    CropType(String name, CropInfo cropInfo) {
        this.name = name;
        this.cropInfo = cropInfo;
    }

    // 작물명으로 해당 작물의 코드 정보를 조회
    public static CropInfo getInfoByName(String cropName) {
        return Arrays.stream(values())
                       .filter(crop -> crop.getName().equals(cropName))
                       .findFirst()
                       .map(CropType::getCropInfo)
                       .orElseThrow(() -> new RuntimeException("지원하지 않는 작물입니다: " + cropName));
    }
}
