package com.example.festimo.domain.user.controller;


import com.example.festimo.domain.user.dto.*;
import com.example.festimo.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterRequestDTO dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format.");
        }
        userService.logout(refreshToken.substring(7));
        return ResponseEntity.ok("Logged out successfully.");
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        return ResponseEntity.ok(userService.changePassword(dto));
    }


    @GetMapping("/user/{email}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
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
