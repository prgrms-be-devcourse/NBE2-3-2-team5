package com.example.festimo.global.config;


import com.example.festimo.domain.oauth.service.CustomOAuth2UserService;
import com.example.festimo.global.utils.jwt.CustomOAuth2FailureHandler;
import com.example.festimo.global.utils.jwt.JwtAuthenticationFilter;
import com.example.festimo.global.utils.jwt.JwtTokenProvider;
import com.example.festimo.global.utils.jwt.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomOAuth2UserService customOAuth2UserService, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler, CustomOAuth2FailureHandler customOAuth2FailureHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.customOAuth2FailureHandler = customOAuth2FailureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // 정적 리소스 허용
                .requestMatchers("/css/**", "/js/**", "/imgs/**","/favicon.ico", "/html/**").permitAll()
                // 추가 정보 입력 페이지 허용
                .requestMatchers(HttpMethod.POST, "/api/register", "/api/login").permitAll()
                .requestMatchers("/api/reviews/**").permitAll()
                .requestMatchers("/oauth2/token").permitAll()
                .requestMatchers("/error").permitAll()// 누구나 가능
                .requestMatchers(HttpMethod.GET, "/api/events").permitAll() // 축제 전체 조회 비회원 허용
                .requestMatchers(HttpMethod.GET, "/api/events/{eventId}").permitAll() // 각각의 축제 조회 비회원 허용
                .requestMatchers(HttpMethod.GET, "/api/events/search").permitAll() // 축제 검색 비회원 허용
                .requestMatchers(HttpMethod.GET, "/api/events/filter/month").permitAll() // 축제 필터링 비회원 허용
                .requestMatchers(HttpMethod.GET, "/api/events/filter/region").permitAll() // 축제 필터링 비회원 허용
                .requestMatchers(HttpMethod.GET, "/api/companions").permitAll() // 게시글 전체 조회는 비회원 허용
                .requestMatchers(HttpMethod.GET, "/api/companions/{postId}").authenticated() // 게시글 상세 조회 인증 필요
                .requestMatchers(HttpMethod.POST, "/api/companions").authenticated() // 게시글 등록 인증 필요
                .requestMatchers(HttpMethod.PUT, "/api/companions/{postId}").authenticated() // 게시글 수정 인증 필요
                .requestMatchers(HttpMethod.DELETE, "/api/companions/{postId}").authenticated() // 게시글 삭제 인증 필요
                .requestMatchers(HttpMethod.POST, "/api/companions/{postId}/comments").authenticated() // 댓글 등록 인증 필요
                .requestMatchers(HttpMethod.PUT, "/api/companions/{postId}/comments/{sequence}").authenticated() // 댓글 수정 인증 필요
                .requestMatchers(HttpMethod.DELETE, "/api/companions/{postId}/comments/{sequence}").authenticated() // 댓글 삭제 인증 필요
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // 권한 기반 접근 제어 관리자만 사용 가능
                .anyRequest().authenticated()    // 나머지는 로그인한 사용자만
            );

        // Add JWT Authentication Filter
        http.addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class
        );

        http
            .oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                    .userService(customOAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(customOAuth2FailureHandler));

        return http.build();
    }
}