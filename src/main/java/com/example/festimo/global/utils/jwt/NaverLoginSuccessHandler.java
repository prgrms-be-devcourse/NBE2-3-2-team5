package com.example.festimo.global.utils.jwt;

import com.example.festimo.domain.user.dto.NaverOauth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class NaverLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    public NaverLoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
     }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        NaverOauth2User naverOauth2User = (NaverOauth2User) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(
                naverOauth2User.getEmail(), naverOauth2User.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(naverOauth2User.getEmail());
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60*60*24*30);

        response.setContentType("application/json");
        response.getWriter().write("{\"access_token\": \"" + accessToken + "\"}");
        response.addCookie(refreshTokenCookie);
        response.setStatus(HttpServletResponse.SC_OK);

    }
}
