package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCropResponseDto {
    private Long id;
    private String cropName;

    public UserCropResponseDto(UserCrop userCrop) {
        this.id = userCrop.getId();
        this.cropName = userCrop.getCropName();
    }
}
