package com.example.rootimpact.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 로그인 응답 DTO")
public class LoginResponse {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "응답 메시지", example = "Login successful")
    private String message;

    public LoginResponse(String email, String token, String message) {
        this.email = email;
        this.token = token;
        this.message = message;
    }
}