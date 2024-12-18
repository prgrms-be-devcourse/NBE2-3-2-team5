package com.example.festimo.post;

import com.example.festimo.domain.post.dto.CommentRequest;
import com.example.festimo.domain.post.dto.CommentResponse;
import com.example.festimo.domain.post.entity.Comment;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.CommentRepository;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.post.service.PostServiceImpl;
import com.example.festimo.exception.PostNotFound;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CreateCommentTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    public CommentRepository commentRepository;

    @Autowired
    private PostServiceImpl postService;

    private Post savedPost;

    @BeforeEach
    void setUp() {
        // 게시글 저장 (댓글 테스트를 위한 기본 게시글)
        savedPost = Post.builder()
                .title("테스트 제목")
                .writer("테스트 작성자")
                .mail("example@example.com")
                .password("1234")
                .content("테스트 내용")
                .build();
        postRepository.save(savedPost);
    }

    @Test
    @DisplayName("댓글 등록 성공")
    void testCreateComment_Success() {
        // Given
        CommentRequest request = new CommentRequest("nickname", "테스트 내용");

        // When
        CommentResponse response = postService.createComment(savedPost.getId(), request);

        // Then
        assertNotNull(response.getId());
        assertEquals("nickname", response.getNickname());
        assertEquals("테스트 내용", response.getComment());
        assertEquals(savedPost.getId(), response.getPostId());

        // DB 확인
        Comment savedComment = commentRepository.findById(response.getId()).orElseThrow();
        assertEquals("테스트 내용", savedComment.getComment());
        assertEquals("nickname", savedComment.getNickname());
        assertEquals(savedPost, savedComment.getPost());
    }

    @Test
    @DisplayName("댓글 등록 시 필수 필드 누락")
    void testCreateComment_MissingFields() {
        // Given
        CommentRequest request = new CommentRequest(null, "테스트 내용");

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            postService.createComment(savedPost.getId(), request);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글 작성")
    void testCreateComment_PostNotFound() {
        // Given
        Long invalidPostId = -1L;
        CommentRequest request = new CommentRequest("nickname", "테스트 내용");

        // When & Then
        assertThrows(PostNotFound.class, () -> {
            postService.createComment(invalidPostId, request);
        });
    }
}