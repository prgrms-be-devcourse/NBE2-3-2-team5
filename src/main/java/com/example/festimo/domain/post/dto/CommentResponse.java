package com.example.festimo.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private Integer sequence;
    private String comment;
    private String nickname;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}