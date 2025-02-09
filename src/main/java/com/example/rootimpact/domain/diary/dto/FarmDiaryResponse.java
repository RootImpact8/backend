package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "일기 응답 DTO")
public class FarmDiaryResponse {

    @Schema(description = "일기 ID", example = "100")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성 날짜 (yyyy-MM-dd)", example = "2023-09-15")
    private LocalDate writeDate;

    @Schema(description = "작물명", example = "Tomato")
    private String cropName;

    @Schema(description = "작업명", example = "Watering")
    private String taskName;

    @Schema(description = "작업 분류", example = "Irrigation")
    private String taskCategory;

    @Schema(description = "일기 내용", example = "Watered the plants thoroughly.")
    private String content;

    // 엔티티를 DTO로 변환하는 생성자
    public FarmDiaryResponse(FarmDiary farmDiary) {
        this.id = farmDiary.getId();
        this.userId = farmDiary.getUser().getId();
        this.writeDate = farmDiary.getWriteDate();
        this.cropName = farmDiary.getUserCrop().getCropName();
        this.taskName = farmDiary.getTask().getName();
        this.taskCategory = farmDiary.getTask().getCategory();
        this.content = farmDiary.getContent();
    }
}