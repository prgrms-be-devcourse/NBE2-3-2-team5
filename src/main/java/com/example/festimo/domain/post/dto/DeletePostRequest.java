package com.example.festimo.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeletePostRequest {
    private String password;

    public DeletePostRequest(String password) {
        this.password = password;
    }
}