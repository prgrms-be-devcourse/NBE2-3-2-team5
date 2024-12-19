package com.example.festimo.global.utils.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {


        // 인증 제외 경로 처리
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/swagger-ui") || requestPath.startsWith("/v3/api-docs") ||
                requestPath.startsWith("/api/register") || requestPath.startsWith("/api/login")) {
            chain.doFilter(request, response);
            return;
        }


        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token));
        }

        chain.doFilter(request, response);
    }
}
