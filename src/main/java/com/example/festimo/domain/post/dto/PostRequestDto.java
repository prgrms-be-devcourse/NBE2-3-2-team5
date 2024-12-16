package com.example.festimo.domain.post.dto;

import com.example.festimo.domain.post.entity.PostCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 50, message = "제목은 50자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "작성자는 필수 입력 항목입니다.")
    private String writer;

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String mail;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;

    private PostCategory category;
}
