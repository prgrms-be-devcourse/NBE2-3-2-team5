package com.example.festimo.global.utils.jwt;

import com.example.festimo.domain.user.dto.NaverOauth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NaverLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    public NaverLoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
     }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        NaverOauth2User naverOauth2User = (NaverOauth2User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateAccessToken(
                naverOauth2User.getEmail(), naverOauth2User.getRole());
        System.out.println("token: " + token);
        response.setHeader("Authorization", "Bearer " + token);
        response.sendRedirect("/");
    }
}
