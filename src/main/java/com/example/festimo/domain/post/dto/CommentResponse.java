package com.example.festimo.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Integer sequence;
    private String comment;
    private String nickname;
    private Long postId;

    public CommentResponse(Integer sequence, String comment, String nickname, Long postId) {
        this.sequence = sequence;
        this.comment = comment;
        this.nickname = nickname;
        this.postId = postId;
    }
}