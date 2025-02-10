package com.example.rootimpact.global.config;

import com.example.rootimpact.global.util.JwtUtil;
import com.example.rootimpact.global.securtiy.JwtAuthenticationFilter; // 경로 수정
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
                .cors(cors -> {
                    // 필요 시, 커스텀 CORS 설정을 추가할 수 있습니다.
                    // 예:
                    // cors.configurationSource(request -> {
                    //     CorsConfiguration config = new CorsConfiguration();
                    //     config.setAllowedOrigins(List.of("*"));
                    //     config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    //     config.setAllowedHeaders(List.of("*"));
                    //     config.setAllowCredentials(true);
                    //     return config;
                    // });
                })
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 메서드에 대한 요청 모두 허용 (preflight 요청 처리)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/h2-console/**"
                        ).permitAll()
                        .requestMatchers("/login", "/register", "/api/user/register", "/api/user/login", "/api/diary/**","/api/farm/price").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}