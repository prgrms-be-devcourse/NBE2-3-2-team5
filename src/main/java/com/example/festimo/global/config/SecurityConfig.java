package com.example.festimo.global.config;


import com.example.festimo.domain.user.service.NaverOauth2UserService;
import com.example.festimo.global.utils.jwt.CustomUserDetailsService;
import com.example.festimo.global.utils.jwt.JwtAuthenticationFilter;
import com.example.festimo.global.utils.jwt.JwtTokenProvider;
import com.example.festimo.global.utils.jwt.NaverLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final NaverOauth2UserService naverOauth2UserService;
    private final NaverLoginSuccessHandler naverLoginSuccessHandler;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, NaverOauth2UserService naverOauth2UserService, NaverLoginSuccessHandler naverLoginSuccessHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.naverOauth2UserService = naverOauth2UserService;
        this.naverLoginSuccessHandler = naverLoginSuccessHandler;
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
                        .requestMatchers(HttpMethod.POST, "/api/register", "/api/login").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/error").permitAll()// 누구나 가능 , "/oauth2/**"
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
//                .oauth2Login()
//                .defaultSuccessUrl("/api/oauth2/success");

        // Add JWT Authentication Filter
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(naverOauth2UserService))
                        .successHandler(naverLoginSuccessHandler));

        return http.build();
    }
}