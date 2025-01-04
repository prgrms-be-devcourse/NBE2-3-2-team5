package com.example.festimo.domain.user.controller;


import com.example.festimo.domain.user.dto.*;
import com.example.festimo.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Tag(name = "회원 관리 API", description = "회원 정보 관리하는 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterRequestDTO dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequestDTO dto) {
        TokenResponseDTO tokenResponseDTO = userService.login(dto);

        String accessToken = tokenResponseDTO.getAccessToken();
        String refreshToken = tokenResponseDTO.getRefreshToken();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict").build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Collections.singletonMap("accessToken", accessToken));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {  // Authorization 헤더 없을 경우, Bearer 토큰 형식 아닐경우
            throw new IllegalArgumentException("Invalid token format.");
        }
        userService.logout(refreshToken.substring(7));  // 앞에 접두사 Bearer 제외
        return ResponseEntity.ok("Logged out successfully.");
    }

    @Operation(summary = "비밀번호 변경")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        return ResponseEntity.ok(userService.changePassword(dto));
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/user/{email}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Operation(summary = "회원 마이페이지 정보 조회")
    @GetMapping("/user/mypage")
    public ResponseEntity<UserResponseDTO> getUser(Authentication authentication) {
        String email = authentication.getName(); // 인증된 사용자의 이메일 가져오기
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


    @Operation(summary = "회원 정보 갱신")
    @PutMapping("/user/update/{email}")
    public ResponseEntity<String> updateUser(@PathVariable String email, @RequestBody UserUpdateRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(email, dto));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/user/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        return ResponseEntity.ok(userService.deleteUser(email));
    }


    //    //현재 인증된 유저 정보 반환
//    @GetMapping("/user")
//    public ResponseEntity<UserResponseDTO> getAuthenticatedUser(@RequestHeader("Authorization") String accessToken) {
//        String email = jwtTokenProvider.getEmailFromToken(accessToken.replace("Bearer ", ""));
//        return ResponseEntity.ok(userService.getUserByEmail(email));
//    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshTokens(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(userService.refreshTokens(refreshToken));
    }


}
