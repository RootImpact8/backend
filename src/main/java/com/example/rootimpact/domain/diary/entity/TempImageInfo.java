package com.example.rootimpact.domain.diary.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TempImageInfo {
    private String originalFileName;
    private String savedFileName;
    private String filePath;
}