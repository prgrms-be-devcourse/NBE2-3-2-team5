package com.example.festimo.domain.post.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponse {
    private Long id;
    private String title;
    private String writer;
    private int views;
    private String createdAt;

    public PostListResponse(Long id, String title, String writer, int views, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.views = views;
        this.createdAt = createdAt.toString();
    }
}