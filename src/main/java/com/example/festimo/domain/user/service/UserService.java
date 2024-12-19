package com.example.festimo.domain.user.service;


import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.dto.UserLoginRequestDTO;
import com.example.festimo.domain.user.dto.UserRegisterRequestDTO;
import com.example.festimo.domain.user.dto.UserResponseDTO;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.global.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    public String register(UserRegisterRequestDTO dto) {

        String email = normalizeEmail(dto.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이메일 중봉");
        }
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("닉네임 중복");
        }

        validatePassword(dto.getPassword());

        User user = User.builder()
                .userName(dto.getUserName())
                .nickname(dto.getNickname())
                .gender(User.Gender.valueOf(dto.getGender())) // 성별 문자열을 Enum으로 변환
                .email(email)
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.USER)
                .ratingAvg(0.0f) // 기본값 설정
                .build();
        userRepository.save(user);

        return "User registered successfully.";
    }

    public String login(UserLoginRequestDTO dto) {

        String email = normalizeEmail(dto.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        // JWT 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // Refresh Token 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return "Access Token: " + accessToken + "\nRefresh Token: " + refreshToken;
    }

    public void logout(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        user.setRefreshToken(null);
        userRepository.save(user);
    }

    public String changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        validatePassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully.";
    }



    // 사용자 정보 반환, 성별 넣을지 말지
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return UserResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // 이메일 소문자로 변환
    private String normalizeEmail(String email) {
        return email != null ? email.toLowerCase() : null;
    }

    // 비밀번호 검증
    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be between 8 and 20 characters.");
        }
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new IllegalArgumentException("Password must contain at least one letter, one number, and one special character.");
        }
    }


    // Refresh Token을 사용해 Access Token을 재발급
    public String refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid Refresh Token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh Token mismatch.");
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        // Refresh Token도 갱신
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return newAccessToken;
    }

}
