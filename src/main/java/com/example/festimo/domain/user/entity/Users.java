package com.example.festimo.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    private String nickname;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private Float ratingAvg;

    public enum Gender { M, F }
    public enum Role { ADMIN, USER }
    public enum Provider { KAKAO, NAVER }
}
