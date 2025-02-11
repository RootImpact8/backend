package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.diary.entity.DiaryImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "일기 이미지 정보 DTO")
public class DiaryImageDto {
    @Schema(description = "원본 파일명", example = "flower.jpg")
    private String originalFileName;

    @Schema(description = "저장된 파일명", example = "123e4567-e89b-12d3-a456-426614174000.jpg")
    private String savedFileName;

    @Schema(description = "파일 저장 경로", example = "C:/Users/username/uploads/123e4567-e89b-12d3-a456-426614174000.jpg")
    private String filePath;

    public DiaryImageDto(DiaryImage image) {
        this.originalFileName = image.getOriginalFileName();
        this.savedFileName = image.getSavedFileName();
        this.filePath = image.getFilePath();
    }
}
