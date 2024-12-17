package com.example.festimo.domain.post.dto;

import com.example.festimo.domain.post.entity.PostCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String writer;
    private String mail;
    private String content;
    private PostCategory category;
}
