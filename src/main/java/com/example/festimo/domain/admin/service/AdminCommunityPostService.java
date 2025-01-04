package com.example.festimo.domain.admin.service;

import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.exception.CustomException;
import com.example.festimo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCommunityPostService {

    private final PostRepository postRepository;

    @Autowired
    public AdminCommunityPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //게시글 삭제
    public void deletePost(Long postId) {

        boolean exists = postRepository.existsById(postId);
        System.out.println("게시글 존재 여부: " + exists);

        if (!postRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND); // 게시글이 없을 경우 예외 발생
        }

        postRepository.deleteById(postId); // 게시글 삭제
    }
}
