package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.dto.CompanionResponse;
import com.example.festimo.domain.meet.entity.Companion;
import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.entity.CompanionMember;
import com.example.festimo.domain.meet.entity.CompanionStatus;
import com.example.festimo.domain.meet.repository.CompanionMemberRepository;
import com.example.festimo.domain.meet.repository.CompanionRepository;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.exception.CompanionNotFoundException;
import com.example.festimo.exception.CustomException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.festimo.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class CompanionService {

    private final CompanionMemberRepository companionMemberRepository;
    private final PostRepository postRepository;
    private final CompanionRepository companionRepository;
    private final UserRepository userRepository;


    private User getUserFromEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    /**
     * 동행 생성
     */
    @Transactional
    public void createCompanion(Long postId, String email) {
        User user = getUserFromEmail(email);

        // post_id 검사
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        // 중복 생성 방지
        companionRepository.findByPost(post)
                .ifPresent(companion -> {
                    throw new CustomException(COMPANION_ALREADY_EXISTS);
                });

        // companion 추가
        LocalDateTime now = LocalDateTime.now();
        Companion companion = new Companion(null, user.getId(), now, post, "동행", CompanionStatus.ONGOING);
        companionRepository.save(companion);

        // companion_member 추가
        addLeaderToCompanionMember(companion.getCompanionId(), user.getId());
    }

    private void addLeaderToCompanionMember(Long companionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        CompanionMemberId companionMemberId = new CompanionMemberId(companionId, userId);
        CompanionMember companionMember = new CompanionMember();
        companionMember.setId(companionMemberId);
        companionMember.setUser(user);
        companionMember.setJoinedDate(LocalDateTime.now());

        companionMemberRepository.save(companionMember);
    }

    /**
     * 동행 취소
     *
     * @param companionId 취소하고 싶은 동행ID
     * @param email 취소하고 싶은 사용자
     */
    @Transactional
    public void deleteCompanion(Long companionId, String email) {
        User user = getUserFromEmail(email);
        CompanionMemberId companionMemberId = new CompanionMemberId(companionId, user.getId());

        if (!companionMemberRepository.existsById(companionMemberId)) {
            throw new CustomException(COMPANION_MEMBER_NOT_FOUND);
        }

        companionMemberRepository.deleteById(companionMemberId);
    }

    /**
     * 리더로 참여한 동행 조회
     *
     * @param leaderId 조회할 리더 id
     * @return CompanionResponse 리더로 참여한 동행들
     */
    @Transactional(readOnly = true)
    public List<CompanionResponse> getCompanionAsLeader(Long leaderId) {
        List<Companion> companions = companionRepository.findByLeaderId(leaderId);

        if (companions == null || companions.isEmpty()) {
            return Collections.emptyList();
        }

        return companions.stream()
                .map(this::mapToCompanionResponse)
                .collect(Collectors.toList());
    }

    /**
     * 동행원으로 참여한 동행 조회
     *
     * @param userId 조회할 user id
     * @return CompanionResponse 동행원으로 참여한 동행들
     */
    @Transactional(readOnly = true)
    public List<CompanionResponse> getCompanionAsMember(Long userId) {
        List<CompanionMember> companionMembers = companionMemberRepository.findByUserId(userId).stream()
                .filter(member -> member.getCompanion() != null && !member.getCompanion().getLeaderId().equals(userId))
                .toList();

        if (companionMembers.isEmpty()) {
            return Collections.emptyList();
        }

        return companionMembers.stream()
                .map(companionMember -> mapToCompanionResponse(companionMember.getCompanion()))
                .collect(Collectors.toList());
    }

    /**
     * Companion을 CompanionResponse로 변환
     */
    private CompanionResponse mapToCompanionResponse(Companion companion) {
        List<CompanionMember> companionMembers = companionMemberRepository
                .findAllByCompanionId(companion.getCompanionId());

        List<CompanionResponse.MemberResponse> members = companionMembers.stream()
                .filter(member -> member.getUser() != null && !member.getUser().getId().equals(companion.getLeaderId()))
                .map(member -> new CompanionResponse.MemberResponse(
                        member.getUser().getId(),
                        member.getUser().getUserName()
                ))
                .collect(Collectors.toList());

        User leader = userRepository.findById(companion.getLeaderId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        return new CompanionResponse(
                companion.getTitle(),
                companion.getCompanionId(),
                companion.getLeaderId(),
                leader.getUserName(),
                members
        );
    }

    /**
     * Companion title 변경
     *
     */
    @Transactional
    public void updateTitle(Long companionId, String title, String email) {
        User user = getUserFromEmail(email);
        validateLeaderAccess(companionId, user.getId());

        Companion companion = companionRepository.findById(companionId)
                .orElseThrow(() -> new CustomException(COMPANION_MEMBER_NOT_FOUND));

        companion.changeTitle(title);// 엔티티 수정

    }

    /**
     * Companion state 변경 (진행중->완료)
     *
     */
    @Transactional
    public void completeCompanion(Long companionId, String email) {
        User user = getUserFromEmail(email);
        validateLeaderAccess(companionId, user.getId());

        Companion companion = companionRepository.findById(companionId)
            .orElseThrow(CompanionNotFoundException :: new);

        if(companion.getStatus() == CompanionStatus.COMPLETED) {
            throw new CustomException(ALREADY_COMPLETED);
        }

        companion.changeStatus(CompanionStatus.COMPLETED);

    }

    /**
     * Companion state 변경 (완료->진행중)
     *
     */
    @Transactional
    public void restoreCompanion(Long companionId, String email) {
        User user = getUserFromEmail(email);
        validateLeaderAccess(companionId, user.getId());

        Companion companion = companionRepository.findById(companionId)
                .orElseThrow(CompanionNotFoundException :: new);

        if (companion.getStatus() == CompanionStatus.ONGOING) {
            throw new CustomException(ALREADY_ONGOING);
        }

        companion.changeStatus(CompanionStatus.ONGOING);
    }




    private void validateLeaderAccess(Long companionId, Long userId) {
        Long leaderId = companionRepository.findLeaderIdByCompanyId(companionId)
                .orElseThrow(() -> new CustomException(COMPANION_NOT_FOUND));

        if (!userId.equals(leaderId)) {
            throw new CustomException(ACCESS_DENIED);
        }
    }


}