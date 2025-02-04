package com.example.rootimpact.global.config;

import com.example.rootimpact.global.util.JwtUtil;
import com.example.rootimpact.global.securtiy.JwtAuthenticationFilter; // 경로 수정
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    // JwtUtil을 생성자 주입
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 허용
                        .requestMatchers("/login", "/register", "/api/user/register","/api/user/login").permitAll()  // 로그인 및 회원가입 허용
                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 콘솔 iframe 허용
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }
}