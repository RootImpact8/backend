package com.example.rootimpact.domain.user.controller;

import com.example.rootimpact.domain.user.dto.LoginRequest;
import com.example.rootimpact.domain.user.dto.LoginResponse;
import com.example.rootimpact.domain.user.dto.RegisterRquest;
import com.example.rootimpact.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "사용자 등록",
            description = "새로운 사용자를 등록합니다."
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody(description = "사용자 등록 요청 객체", required = true)
            @org.springframework.web.bind.annotation.RequestBody RegisterRquest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(
            summary = "사용자 로그인",
            description = "사용자 이메일과 비밀번호를 이용하여 로그인합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody(description = "로그인 요청 객체", required = true)
            @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

}