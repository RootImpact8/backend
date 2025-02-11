package com.example.rootimpact.domain.diary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diaryimage")
public class DiaryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_diary_id")
    private FarmDiary farmDiary;

    private String originalFileName;
    private String savedFileName;
    private String filePath;

    @Builder
    public DiaryImage(FarmDiary farmDiary, String originalFileName, String savedFileName, String filePath) {
        this.farmDiary = farmDiary;
        this.originalFileName = originalFileName;
        this.savedFileName = savedFileName;
        this.filePath = filePath;
    }

    public void setFarmDiary(FarmDiary farmDiary) {
        this.farmDiary = farmDiary;
    }

}
