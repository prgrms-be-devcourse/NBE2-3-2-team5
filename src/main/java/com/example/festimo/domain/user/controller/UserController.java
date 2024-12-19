package com.example.festimo.domain.user.controller;


import com.example.festimo.domain.user.dto.UserLoginRequestDTO;
import com.example.festimo.domain.user.dto.UserRegisterRequestDTO;
import com.example.festimo.domain.user.dto.UserResponseDTO;
import com.example.festimo.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginRequestDTO dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String refreshToken) {
        userService.logout(refreshToken.replace("Bearer ", ""));
        return ResponseEntity.ok("Logged out successfully.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        return ResponseEntity.ok(userService.changePassword(email, oldPassword, newPassword));
    }


    @GetMapping("/user/{email}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

//    //현재 인증된 유저 정보 반환
//    @GetMapping("/user")
//    public ResponseEntity<UserResponseDTO> getUser(@RequestHeader("Authorization") String accessToken) {
//        String email = jwtTokenProvider.getEmailFromToken(accessToken.replace("Bearer ", ""));
//        return ResponseEntity.ok(userService.getUserByEmail(email));
//    }


    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String newAccessToken = userService.refreshAccessToken(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }
}
