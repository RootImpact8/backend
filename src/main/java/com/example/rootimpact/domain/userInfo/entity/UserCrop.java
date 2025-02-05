package com.example.rootimpact.domain.userInfo.entity;

import com.example.rootimpact.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_crop")  // ✅ 소문자로 테이블명 설정
public class UserCrop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)  // ✅ 소문자로 설정
    private User user;

    @Column(name = "crop_name") // ✅ 소문자로 설정
    private String cropName;

    @Column(name = "is_interest_crop") // ✅ 소문자로 설정
    private boolean isInterestCrop;
}