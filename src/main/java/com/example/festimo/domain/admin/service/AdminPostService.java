package com.example.festimo.domain.admin.service;

import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.festimo.exception.ErrorCode.POST_NOT_FOUND;

@Service
public class AdminPostService {

    private final PostRepository postRepository;

    public AdminPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public void deletePostById(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow( () -> new CustomException(POST_NOT_FOUND));
        postRepository.delete(post);
    }
}
