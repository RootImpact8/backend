package com.example.rootimpact.domain.userInfo.entity;

import com.example.rootimpact.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_location")  // ✅ 소문자로 테이블명 설정
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // ✅ 소문자로 설정
    @JsonIgnore //유저정보숨기려고
    private User user;

    @Column(name = "city") // ✅ 소문자로 설정
    private String city;

    @Column(name = "state") // ✅ 소문자로 설정
    private String state;

    @Column(name = "country") // ✅ 소문자로 설정
    private String country;
}