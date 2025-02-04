package com.example.rootimpact.global.securtiy;

import com.example.rootimpact.global.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 로그인과 회원가입 요청은 JWT 인증을 요구하지 않음
        String uri = request.getRequestURI();
        if (uri.equals("/login") || uri.equals("/register") || uri.startsWith("/api/user/register")||uri.startsWith("/api/user/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");

        // Authorization 헤더에서 Bearer 토큰 추출
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
            System.out.println("Extracted Token: " + token);

            // JWT 토큰 검증
            if (jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
                // 사용자 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(jwtUtil.extractUsername(token), null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}