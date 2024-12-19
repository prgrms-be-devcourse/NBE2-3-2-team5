package com.example.festimo.domain.user.dto;

import lombok.Data;

@Data
public class UserLoginRequestDTO {
    private String email;
    private String password;
}
