package com.example.rootimpact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()  // 🔥 H2 콘솔 허용
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // 🔥 CSRF 비활성화
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // 🔥 H2 콘솔 iframe 허용
        return http.build();
    }

}

