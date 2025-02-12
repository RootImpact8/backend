package com.example.rootimpact.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "임시 저장된 이미지 정보 응답")
public class TempImageResponse {

    @Schema(description = "원본 파일명", example = "flower.jpg")
    private String originalFileName;

    @Schema(description = "저장된 파일명", example = "123e4567-e89b-12d3-a456-426614174000.jpg")
    private String savedFileName;

    @Schema(description = "파일 저장 경로", example = "/uploads/123e4567-e89b-12d3-a456-426614174000.jpg")
    private String filePath;

}
