package com.example.festimo.admin.service;

import com.example.festimo.admin.repository.CommunityPostsRepository;
import com.example.festimo.exception.CustomException;
import com.example.festimo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCommunityPostService {

    private final CommunityPostsRepository communityPostsRepository;

    @Autowired
    public AdminCommunityPostService(CommunityPostsRepository communityPostsRepository) {
        this.communityPostsRepository = communityPostsRepository;
    }

    //게시글 삭제
    public void deletePost(Long postId) {

        boolean exists = communityPostsRepository.existsById(1L);
        System.out.println("게시글 존재 여부: " + exists);


        if (!communityPostsRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND); // 게시글이 없을 경우 예외 발생
        }


        communityPostsRepository.deleteById(postId); // 게시글 삭제
    }
}
