package com.example.rootimpact.domain.userInfo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "crop")  // 기본 작물 데이터 테이블
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 작물 이름은 고유해야 함
    private String name;
}