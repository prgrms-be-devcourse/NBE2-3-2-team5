package com.example.festimo.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
}
