package com.example.festimo.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {
    private String userName;
    private String nickname;
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "적어도 숫자 하나, 문자 하나, 특수문자 하나")
    private String password;

    @Pattern(regexp = "M|F", message = "'M' or 'F'")
    private String gender;

}
