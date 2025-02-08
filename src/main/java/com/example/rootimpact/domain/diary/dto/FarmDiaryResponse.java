package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FarmDiaryResponse {
    private Long id;
    private Long userId;
    private LocalDate writeDate;
    private String cropName;
    private String taskName;
    private String taskCategory;
    private String content;

    // 엔티티를 DTO로 변환
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
