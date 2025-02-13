package com.example.rootimpact.domain.user.service;

import com.example.rootimpact.domain.user.dto.LoginRequest;
import com.example.rootimpact.domain.user.dto.LoginResponse;
import com.example.rootimpact.domain.user.dto.RegisterRequest;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.exception.UserNotFoundException;
import com.example.rootimpact.domain.user.repository.UserRepository;
import com.example.rootimpact.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;  // `JwtUtil`을 주입받도록 수정

    // 사용자 회원가입
    public void registerUser(RegisterRequest registerRequest) {
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        // user.setName(registerRquest.getName()); // 필요 시 활성화
        // user.setRegion(registerRquest.getRegion()); // 지역 정보 저장

        userRepository.save(user);
    }

    // 사용자 로그인 & JWT 토큰 발급
    public LoginResponse login(LoginRequest loginRequest) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // JWT 토큰 생성 (jwtUtil을 인스턴스로 사용)
        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(user.getEmail(), token, "로그인 성공 및 토큰 발급");
    }
}