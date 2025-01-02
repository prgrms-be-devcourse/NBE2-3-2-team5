package com.example.festimo.domain.user.service;


import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.dto.*;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.global.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    // 회원가입
    public String register(UserRegisterRequestDTO dto) {

        String email = normalizeEmail(dto.getEmail());
        logger.info("Attempting to register user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            logger.warn("Registration failed. Email already exists: {}", email);
            throw new IllegalArgumentException("이메일 중복");
        }

        if (userRepository.existsByNickname(dto.getNickname())) {
            logger.warn("Registration failed. Nickname already exists: {}", dto.getNickname());
            throw new IllegalArgumentException("닉네임 중복");
        }

        validatePassword(dto.getPassword());

//        User user = User.builder()
//                .userName(dto.getUserName())
//                .nickname(dto.getNickname())
//                .gender(User.Gender.valueOf(dto.getGender())) // 성별 문자열을 Enum으로 변환
//                .email(email)
//                .password(passwordEncoder.encode(dto.getPassword()))
//                .role(User.Role.USER)
//                .ratingAvg(0.0f) // 기본값 설정
//                .build();

        // DTO → Entity 변환
        User user = modelMapper.map(dto, User.class);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.USER);
        user.setRatingAvg(0.0f); // 기본값 설정


        userRepository.save(user);
        logger.info("User registered successfully with email: {}", email);
        return "User registered successfully.";
    }

    public TokenResponseDTO login(UserLoginRequestDTO dto) {
        String email = normalizeEmail(dto.getEmail());
        logger.info("Attempting login for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed. User not found for email: {}", email);
                    return new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다..");
                });

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            logger.warn("Login failed. Invalid credentials for email: {}", email);
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        TokenResponseDTO tokens = regenerateTokens(user);
        user.setRefreshToken(tokens.getRefreshToken());
        System.out.println("리프레쉬 토큰 : " + tokens.getRefreshToken());
        userRepository.save(user);
        System.out.println(user);
        logger.info("Login successful for email: {}", email);
        return tokens;
    }

    public void logout(String refreshToken) {
        logger.info("Attempting logout for refresh token.");
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Logout failed. Invalid refresh token.");
                    return new BadCredentialsException("Invalid refresh token.");
                });

        user.setRefreshToken(null);
        userRepository.save(user);

        logger.info("Logout successful.");
    }

    public String changePassword(ChangePasswordDTO dto) {
        // 이메일 정규화 및 사용자 조회
        User user = userRepository.findByEmail(normalizeEmail(dto.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // 기존 비밀번호 검증
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // 새 비밀번호 유효성 검사
        validatePassword(dto.getNewPassword());

        // 비밀번호 변경 및 저장
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully.";
    }



    // 사용자 정보 반환, 성별 넣을지 말지
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        return UserResponseDTO.builder()
//                .id(user.getId())
//                .userName(user.getUserName())
//                .nickname(user.getNickname())
//                .email(user.getEmail())
//                .role(user.getRole().name())
//                .build();
        return modelMapper.map(user, UserResponseDTO.class);
    }

    // 이메일 소문자로 변환
    private String normalizeEmail(String email) {
        return email != null ? email.toLowerCase() : null;
    }

    // 비밀번호 검증
    private void validatePassword(String password) {
        // 비밀번호가 null인 경우 예외 발생
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        // 비밀번호 길이 검사
        if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be between 8 and 20 characters.");
        }

        // 비밀번호에 문자 포함 여부 검사
        if (!password.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one letter.");
        }

        // 비밀번호에 숫자 포함 여부 검사
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one number.");
        }

        // 비밀번호에 특수문자 포함 여부 검사
        if (!password.matches(".*[@$!%*?&].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (@, $, !, %, *, ?, &).");
        }
    }



    // Refresh Token을 사용해 Access Token을 재발급
    public TokenResponseDTO refreshTokens(String refreshToken) {
        logger.info("Attempting to refresh tokens.");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            logger.warn("Token refresh failed. Invalid refresh token.");
            throw new IllegalArgumentException("Invalid Refresh Token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Token refresh failed. User not found for email: {}", email);
                    return new IllegalArgumentException("User not found.");
                });

        if (!refreshToken.equals(user.getRefreshToken())) {
            logger.warn("Token refresh failed. Refresh token mismatch for email: {}", email);
            throw new IllegalArgumentException("Refresh Token mismatch.");
        }

        TokenResponseDTO tokens = regenerateTokens(user);
        logger.info("Tokens refreshed successfully for email: {}", email);
        return tokens;
    }

    private TokenResponseDTO regenerateTokens(User user) {
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new TokenResponseDTO(newAccessToken, newRefreshToken);
    }

}
