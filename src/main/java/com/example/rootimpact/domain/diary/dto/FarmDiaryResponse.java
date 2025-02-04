package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class FarmDiaryResponse {
    private Long id;
    private Long userId;
    private LocalDate writeDate;
    //작물
    //작업
    private String content;
    //이미지

    // 엔티티를 DTO로 변환
    public FarmDiaryResponse(FarmDiary farmDiary) {
        this.id = farmDiary.getId();
        this.userId = farmDiary.getUser().getId();
        this.writeDate = farmDiary.getWriteDate();
        //작물
        //작업
        this.content = farmDiary.getContent();
        //이미지
    }
}
