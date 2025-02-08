package com.example.rootimpact.domain.diary.entity;

import com.example.rootimpact.domain.userInfo.entity.Crop;
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
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    private String category;

    @Builder
    public Task(String Name, Crop crop, String category) {
        this.Name = Name;
        this.crop = crop;
        this.category = category;
    }
}
