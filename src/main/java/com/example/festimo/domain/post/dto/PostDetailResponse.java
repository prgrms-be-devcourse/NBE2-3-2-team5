package com.example.festimo.domain.post.dto;

import com.example.festimo.domain.post.entity.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {
    private Long id;
    private String title;
    private String writer;
    private String mail;
    private String content;
    private PostCategory category;
    private int views;
    private String createdAt;

    public PostDetailResponse(Long id, String title, String writer, String mail, String content,
                              PostCategory category, int views, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.mail = mail;
        this.content = content;
        this.category = category;
        this.views = views;
        this.createdAt = createdAt.toString();
    }
}