package com.example.festimo.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AdminUpdateUserDTO {

    private String userName;
    private String nickname;
    private String email;
    private String gender; // F,M
    private Float ratingAvg;
}
