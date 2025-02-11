package com.example.rootimpact.domain.user.service;

import com.example.rootimpact.domain.user.dto.LoginRequest;
import com.example.rootimpact.domain.user.dto.LoginResponse;
import com.example.rootimpact.domain.user.dto.RegisterRquest;
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

    public void registerUser(RegisterRquest registerRquest){
        Optional<User> existingUser = userRepository.findByEmail(registerRquest.getEmail());
        if (existingUser.isPresent()) {
            // 중복된 이메일이 있을 경우 예외 처리 (예: 이메일 이미 존재)
            throw new RuntimeException("Email already exists");
        }
        String encodedPassword = passwordEncoder.encode(registerRquest.getPassword());
        User user = new User();
        user.setEmail(registerRquest.getEmail());
        //user.setName(registerRquest.getName());
        user.setPassword(encodedPassword);
        //user.setRegion(registerRquest.getRegion()); // 지역 정보 저장
        userRepository.save(user);
    }
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> optional = userRepository.findByEmail(loginRequest.getEmail());
        if (optional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        User user =optional.get();
        // 비밀번호 비교
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        // 로그인 성공 시, JWT 토큰 생성

        String token = JwtUtil.generateToken(user.getEmail());// JWT 토큰을 생성하는 유틸리티 사용

        return new LoginResponse(user.getEmail(), token,"로그인성공 및 토큰발급");
    }
}
