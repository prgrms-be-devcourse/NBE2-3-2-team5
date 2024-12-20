package com.example.festimo.post;

import com.example.festimo.domain.post.dto.PostRequest;
import com.example.festimo.domain.post.dto.PostListResponse;
import com.example.festimo.domain.post.entity.PostCategory;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.post.service.PostServiceImpl;
import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class CreatePostTest {

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 유저 생성 및 저장
        User user = User.builder()
                .userName("user")
                .email("example@example.com")
                .nickname("nickname")
                .password("password1234")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        // SecurityContext에 인증 정보 설정
        authentication = new TestingAuthenticationToken(user.getEmail(), null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("게시글 등록 성공")
    void testCreatePost_Success() {
        // Given
        PostRequest request = PostRequest.builder()
                .title("테스트 제목")
                .writer("nickname")
                .mail("example@example.com")
                .password("1234")
                .content("테스트 내용")
                .build();

        // When
        PostListResponse response = postService.createPost(request, SecurityContextHolder.getContext().getAuthentication());

        // Then
        assertNotNull(response.getId());
    }

    @Test
    @DisplayName("게시글 등록 시 필수 필드 누락")
    void testCreatePost_MissingFields() {
        // Given
        PostRequest request = PostRequest.builder()
                .writer("작성자")
                .mail("example@example.com")
                .password("1234")
                .content("내용")
                .build(); // 제목 누락

        // When & Then
        assertThatThrownBy(() -> postService.createPost(request, authentication))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("제목은 필수 입력 항목입니다.");
    }

    @Test
    @DisplayName("잘못된 데이터로 게시글 등록 실패")
    void testCreatePost_InvalidData() {
        // Given
        PostRequest request = PostRequest.builder()
                .title("a".repeat(51)) // 제목 길이 초과
                .writer("작성자")
                .mail("example@example.com")
                .password("1234")
                .content("내용")
                .build();

        // When & Then
        assertThatThrownBy(() -> postService.createPost(request, authentication))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("제목은 30자 이하로 입력해주세요.");
    }

    @Test
    @DisplayName("비밀번호 형식이 맞지 않아 게시글 등록 실패")
    void failToPostWhenPasswordInvalid() {
        // Given
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("테스트 게시글 제목");
        postRequest.setWriter("작성자1");
        postRequest.setMail("test@example.com");
        postRequest.setPassword("abc");
        postRequest.setContent("테스트 내용입니다.");
        postRequest.setCategory(PostCategory.QNA);

        // When & Then
        assertThatThrownBy(() -> postService.createPost(postRequest, authentication))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("비밀번호는 4자 이상 20자 이하로 입력해주세요.");
    }
}