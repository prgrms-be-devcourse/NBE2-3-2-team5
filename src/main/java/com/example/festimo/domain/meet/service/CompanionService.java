package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.entity.Companion;
import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.repository.CompanionMemberRepository;
import com.example.festimo.domain.meet.repository.CompanionRepository;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.exception.CustomException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.festimo.exception.ErrorCode.*;

@Service
public class CompanionService {

    private final CompanionMemberRepository companionMemberRepository;
    private final PostRepository postRepository;
    private final CompanionRepository companionRepository;

    public CompanionService(CompanionMemberRepository companionMemberRepository, PostRepository postRepository, CompanionRepository companionRepository) {
        this.companionMemberRepository = companionMemberRepository;
        this.postRepository = postRepository;
        this.companionRepository = companionRepository;
    }

    @Transactional
    public void createCompanion(Long postId, Long userId) {

        //post_id 검사
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException( POST_NOT_FOUND));

        // 중복 생성 방지
        companionRepository.findByPost(post)
                .ifPresent(companion -> {
                    throw new CustomException(COMPANION_ALREADY_EXISTS);
                });

        //companion 생성
        LocalDateTime now = LocalDateTime.now();
        Companion companion = new Companion(null, userId, now, post);
        companionRepository.save(companion);
    }




    /**
     * 동행 취소
     *
     * @param companionId  취소하고 싶은 동행ID
     * @param userId     취소하고 싶은 듀저 ID
     */
    @Transactional
    public void deleteCompaion(Long companionId, Long userId) {

        CompanionMemberId companionMemberId = new CompanionMemberId(companionId, userId);

        //동행에 참가 여부 확인
        if(!companionMemberRepository.existsById(companionMemberId)){
            throw new CustomException(COMPANION_NOT_FOUND);
        }

        //동행원에서 삭제
        companionMemberRepository.deleteById(companionMemberId);
    }

}
