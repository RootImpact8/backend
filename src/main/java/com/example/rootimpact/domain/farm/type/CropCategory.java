package com.example.rootimpact.domain.farm.type;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CropCategory {
    STRAWBERRY(1L,"딸기", "VC010804"),
    RICE(2L,"벼", "FC010106"),
    POTATO(3L,"감자", "FC050501"),
    LETTUCE(4L,"상추", "VC021005"),
    APPLE(5L,"사과", "FT010601"),
    PEPPER(6L,"고추", "VC011205");

    private final Long id; // 작물 ID
    private final String cropName; // 작물명
    private final String code; // subCategoryCode

    public static String getCodeById(Long id) {
        return Arrays.stream(CropCategory.values())
                       .filter(category -> category.getId().equals(id))
                       .findFirst()
                       .map(CropCategory::getCode)
                       .orElseThrow(() -> new IllegalArgumentException("Invalid crop name: " + id));
    }
}
