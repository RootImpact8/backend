package com.example.rootimpact.domain.diary.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class FarmDiaryRequest {
    private Long userId;
    //작물
    //작업
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate writeDate;
}
