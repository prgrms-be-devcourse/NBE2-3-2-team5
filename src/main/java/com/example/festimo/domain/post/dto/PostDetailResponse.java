package com.example.festimo.domain.post.dto;

import com.example.festimo.domain.post.entity.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
    private List<CommentResponse> comments = new ArrayList<>();
}