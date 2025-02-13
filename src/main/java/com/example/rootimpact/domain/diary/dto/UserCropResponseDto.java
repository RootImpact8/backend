package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 작물 응답 DTO")
public class UserCropResponseDto {

    @Schema(description = "사용자 작물 ID", example = "5")
    private Long id;

    @Schema(description = "작물명", example = "감자")
    private String cropName;

    @Schema(description = "작물 ID", example = "3")
    private Long cropId;

    public UserCropResponseDto(UserCrop userCrop) {
        this.id = userCrop.getId();
        this.cropName = userCrop.getCropName();
        this.cropId = userCrop.getCropId();
    }
}