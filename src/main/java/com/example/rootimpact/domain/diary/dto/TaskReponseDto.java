package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.diary.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "작업 유형 응답 DTO")
public class TaskReponseDto {

    @Schema(description = "작업 ID", example = "10")
    private Long id;

    @Schema(description = "작업 이름", example = "1차~~")
    private String name;

    @Schema(description = "작업 분류", example = "휴식")
    private String category;

    public TaskReponseDto(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.category = task.getCategory();
    }
}