package com.example.festimo.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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