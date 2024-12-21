package com.example.festimo.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {
    private String userName;
    private String nickname;
    private String email;
    private String password;
    private String gender;

}
