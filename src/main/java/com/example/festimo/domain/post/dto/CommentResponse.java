package com.example.festimo.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String comment;
    private String nickname;
    private Long postId;

    public CommentResponse(Long id, String comment, String nickname, Long postId) {
        this.id = id;
        this.comment = comment;
        this.nickname = nickname;
        this.postId = postId;
    }
}