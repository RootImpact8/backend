package com.example.rootimpact.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRquest {
    private String email;
    private String password;
    private String name;
    private String region; // 지역 정보 추가
}
