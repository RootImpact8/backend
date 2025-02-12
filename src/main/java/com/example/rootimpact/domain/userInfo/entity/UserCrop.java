package com.example.rootimpact.domain.userInfo.entity;

import com.example.rootimpact.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_crop")
public class UserCrop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "crop_name")
    private String cropName;

    @Column(name = "crop_id")  // 추가
    private Long cropId;       // 추가

    @Column(name = "is_interest_crop")
    private boolean isInterestCrop;
}