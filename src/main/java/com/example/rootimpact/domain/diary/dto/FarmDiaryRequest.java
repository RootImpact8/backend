package com.example.rootimpact.domain.diary.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
public class FarmDiaryRequest {
    private Long userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate writeDate;
    private String userCropName;
    private Long taskId;
    private String content;
}
