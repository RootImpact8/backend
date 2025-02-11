package com.example.rootimpact.global.config;

import com.example.rootimpact.global.util.JwtUtil;
import com.example.rootimpact.global.securtiy.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

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
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        // ✅ OPTIONS 요청 모두 허용 (CORS 관련 문제 해결)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // ✅ Swagger 관련 경로 허용
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()
                        // ✅ 로그인, 회원가입, 가격 조회 API는 인증 없이 허용
                        .requestMatchers(
                                "/login", "/register",
                                "/api/user/register", "/api/user/login","/api/dairy/**",
                                "/api/farm/price"
                        ).permitAll()
                        // ✅ 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // ✅ JWT 인증 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}