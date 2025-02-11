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

        String uri = request.getRequestURI();

        // ✅ Swagger 및 인증이 필요 없는 경로는 필터링 제외
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-resources") || uri.startsWith("/webjars") ||
                uri.equals("/login") || uri.equals("/register") || uri.startsWith("/api/diary/**") ||
                uri.startsWith("/api/user/register") || uri.startsWith("/api/user/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");

        // ✅ JWT 토큰 검증 후 인증 처리
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            if (jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(jwtUtil.extractUsername(token), null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}