package com.example.festimo.domain.meet.controller;

import com.example.festimo.domain.meet.dto.CompanionRequest;
import com.example.festimo.domain.meet.dto.CompanionResponse;
import com.example.festimo.domain.meet.service.CompanionService;
import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.exception.CustomException;
import com.example.festimo.global.utils.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.festimo.exception.ErrorCode.USER_NOT_FOUND;

@RestController
@RequestMapping("api/meet")
@Tag(name = "동행 API", description = "동행 관련 API")
public class CompanionController {

    CompanionService companionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public CompanionController(CompanionService companionService,JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {

        this.companionService = companionService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * 동행 게시글 생성하면 동행 생성
     *
     */
    @PostMapping("/companions")
    @Operation(summary = "동행 생성")
    public ResponseEntity<Void> createCompanion(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CompanionRequest request ) {

        //jwt에서 이메일 추출
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

       //companionService.createCompanion(request.getPostId(), request.getUserId());
        companionService.createCompanion(request.getPostId(),email);
       return ResponseEntity.status(HttpStatus.CREATED).build();

    }


    /**
     * 동행원의 동행 취소
     *
     * @param companionId 취소할 동행의 ID
    //* @param userId    취소할 유저의 ID --> 로그인 하고 바꾸기!!!
     */
   // @DeleteMapping("/{companionId}/users/{userId}")
    @DeleteMapping("/{companionId}")
    @Operation(summary = "동행 취소")
    public ResponseEntity<Void>  deleteCompaion(
            @PathVariable Long companionId,
            //@PathVariable Long userId
            @RequestHeader("Authorization") String authorizationHeader
    ){

        //jwt에서 이메일 추출
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        companionService.deleteCompaion(companionId, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * 내 동행 찾기
     *
     */
   // @GetMapping("/companions/mine/{userId}")
    @GetMapping("/companions/mine")
    @Operation(summary = "내 동행 찾기")
    public ResponseEntity<Map<String,Object>> getMyCompanions(
           // @PathVariable Long userId
           @RequestHeader("Authorization") String authorizationHeader
    ){

        //jwt에서 이메일 추출
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        //userId 추출
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(USER_NOT_FOUND));

        Long userId = user.getId();

        // 검증: 유저가 존재하지 않으면 예외 발생
        User validUser = companionService.validateAndGetUser(userId);


        //리더로 참여한 동행
        List<CompanionResponse> asLeader = companionService.getCompanionAsLeader(userId);

        //동행원으로 참여한 동행
        List<CompanionResponse> asMember = companionService.getCompanionAsMember(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("asLeader", asLeader);
        response.put("asMember", asMember);

        return ResponseEntity.ok(response);
    }
}
