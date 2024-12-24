package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.entity.Companion;
import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.entity.Companion_member;
import com.example.festimo.domain.meet.repository.CompanionMemberRepository;
import com.example.festimo.domain.meet.repository.CompanionRepository;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public CompanionService(CompanionMemberRepository companionMemberRepository, PostRepository postRepository, CompanionRepository companionRepository,UserRepository userRepository) {
        this.companionMemberRepository = companionMemberRepository;
        this.postRepository = postRepository;
        this.companionRepository = companionRepository;
        this.userRepository = userRepository;
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

        //companion 추가
        LocalDateTime now = LocalDateTime.now();
        Companion companion = new Companion(null, userId, now, post);
        companionRepository.save(companion);

        //companion_member 추가
        addLeaderToCompanionMember(companion.getCompanionId(), userId);
    }

    private void addLeaderToCompanionMember(Long companionId, Long userId) {
        // leader를 companion_member에 저장
        CompanionMemberId companionMemberId = new CompanionMemberId(companionId, userId);

        Companion_member companionMember = new Companion_member();
        companionMember.setId(companionMemberId);
        companionMember.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND)));
        companionMember.setJoinedDate(LocalDateTime.now());

        companionMemberRepository.save(companionMember);
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
