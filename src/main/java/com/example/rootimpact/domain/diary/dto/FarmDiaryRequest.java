package com.example.rootimpact.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
@Schema(description = "일기 생성 및 수정 요청 DTO")
public class FarmDiaryRequest {

    @Schema(description = "사용자 ID", example = "1", required = true)
    private Long userId;

    @Schema(description = "작성 날짜 (yyyy-MM-dd)", example = "2023-09-15", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate writeDate;

    @Schema(description = "사용자 작물명", example = "감자", required = true)
    private String userCropName;

    @Schema(description = "작업 ID", example = "2", required = true)
    private Long taskId;

    @Schema(description = "일기 내용", example = "Planted new seeds.", required = true)
    private String content;
}