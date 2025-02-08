package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.diary.entity.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskReponseDto {

    private Long id;
    private String name;
    private String category;

    public TaskReponseDto(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.category = task.getCategory();
    }

}
