package com.example.festimo.domain.user.controller;


import com.example.festimo.domain.user.dto.*;
import com.example.festimo.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO dto) {
        return ResponseEntity.ok(userService.login(dto));
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
