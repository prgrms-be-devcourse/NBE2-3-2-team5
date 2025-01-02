package com.example.festimo.global.utils.jwt;

import com.example.festimo.domain.oauth.dto.CustomOAuth2User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtTokenProvider jwtTokenProvider;

	public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		// 인증된 사용자 정보 가져오기
		CustomOAuth2User oauth2User = (CustomOAuth2User)authentication.getPrincipal(); // 수정됨

		// 이메일 확인
		String email = oauth2User.getEmail();
		System.out.println(email);

		// 이메일이 정상적으로 제공된 경우 JWT 토큰 생성
		String role = oauth2User.getAuthorities().iterator().next().getAuthority(); // 역할 가져오기
		String token = jwtTokenProvider.generateAccessToken(email, role);

		System.out.println("Generated Access Token: " + token);

		// 헤더에 JWT 추가
		response.setHeader("Authorization", "Bearer " + token);
		System.out.println("Authorization : Bearer " + token);

		// 홈 화면으로 이동
		getRedirectStrategy().sendRedirect(request, response, "/html/festival.html?accessToken=" + token);
	}
}

//    // OAuth2User 정보 가져오기
//    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//    String email = (String) oAuth2User.getAttributes().get("email");
//
//    // JWT 생성
//    String accessToken = jwtTokenProvider.generateAccessToken(email, "USER");
//    String refreshToken = jwtTokenProvider.generateRefreshToken(email);
//
//
//    // 응답에 JWT 포함
//        response.setContentType("application/json");
//        response.getWriter().write(
//            String.format("{\"accessToken\": \"%s\", \"refreshToken\": \"%s\"}", accessToken, refreshToken)
//        );