package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.dto.CompanionResponse;
import com.example.festimo.domain.meet.entity.Companion;
import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.entity.Companion_member;
import com.example.festimo.domain.meet.repository.CompanionMemberRepository;
import com.example.festimo.domain.meet.repository.CompanionRepository;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.exception.CustomException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

import java.util.List;

import java.util.stream.Collectors;

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


    /**
     * 동행 생성
     *
     *
     */
    @Transactional
   // public void createCompanion(Long postId, Long userId) {
    public void createCompanion(Long postId, String email) {

        //userId 추출
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(USER_NOT_FOUND));

        Long userId = user.getId();

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
     * @param email     취소하고 싶은 듀저
     */
    @Transactional
    public void deleteCompaion(Long companionId, String email) {

        //userId 추출
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(USER_NOT_FOUND));

        Long userId = user.getId();

        CompanionMemberId companionMemberId = new CompanionMemberId(companionId, userId);

        //동행에 참가 여부 확인
        if(!companionMemberRepository.existsById(companionMemberId)){
            throw new CustomException(COMPANION_NOT_FOUND);
        }

        //동행원에서 삭제
        companionMemberRepository.deleteById(companionMemberId);
    }

    /**
     * 유저 존재하는지 조회
     *
     * @param userId   조회할 유저 id
     *
     */
    public User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND)); // USER_NOT_FOUND 예외 반환
    }

    /**
     * 리더로 참여한 동행 조회
     *
     * @param leaderId   조회할 리더 id
     * @return CompanionResponse 리더로 참여한 동행들
     */
    public List<CompanionResponse> getCompanionAsLeader(Long leaderId){

        //리더로 참여한 동행
        List<Companion> companions = companionRepository.findByLeaderId(leaderId);

        //데이터 변환
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
     * @param userId  조회할 user id
     * @return CompanionResponse 동행원으로 참여한 동행들
     */
    public List<CompanionResponse> getCompanionAsMember(Long userId){

        //사용자가 멤버로 포함된 동행
        List<Companion_member> companionMembers = companionMemberRepository.findByUserId(userId).stream()
                .filter(member -> member.getCompanion() != null && !member.getCompanion().getLeaderId().equals(userId)) // 리더로 속한 동행 제외
                .collect(Collectors.toList());

        if (companionMembers.isEmpty()) {
            return Collections.emptyList();
        }

        //데이터 변환
        return companionMembers.stream()
                .map(companionMember -> mapToCompanionResponse(companionMember.getCompanion()))
                .collect(Collectors.toList());
    }

    /**
     * Companion_member에서 CompanionResponse로 변환
     *
     *
     */
    private CompanionResponse mapToCompanionResponse(Companion companion) {

        //동행에 포함된 멤버 가져오기
        List<Companion_member> companionMembers = companionMemberRepository.findAllByCompanionId(companion.getCompanionId());

        //변환
        List<CompanionResponse.MemberResponse> members = companionMembers.stream()
                .filter(member -> member.getUser() != null && !member.getUser().getId().equals(companion.getLeaderId())) // 리더 제외
                .map(member -> new CompanionResponse.MemberResponse(
                        member.getUser().getId(),
                        member.getUser().getUserName()
                ))
                .collect(Collectors.toList());


        //리더 정보 가져오기
        User leader = userRepository.findById(companion.getLeaderId())
                .orElseThrow(()->new CustomException(USER_NOT_FOUND));

        return new CompanionResponse(
                companion.getCompanionId(),
                companion.getLeaderId(),
                leader !=null ? leader.getUserName() : null,
                members
        );
    }


}
