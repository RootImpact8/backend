package com.example.rootimpact.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String email;
    private String token; //jwt
    private String message;

    public LoginResponse(String email, String token,String message) {
        this.email = email;
        this.token = token;
        this.message=message;// token 값을 설정
    }
}
