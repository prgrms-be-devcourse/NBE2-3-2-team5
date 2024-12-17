package com.example.festimo.post;

import com.example.festimo.domain.post.dto.UpdatePostRequest;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.entity.PostCategory;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.post.service.PostServiceImpl;
import com.example.festimo.exception.InvalidPasswordException;
import com.example.festimo.exception.PostNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UpdatePostTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private ModelMapper modelMapper;

    private Post existingPost;

    @BeforeEach
    void setUp() {
        // Given
        existingPost = Post.builder()
                .title("Original Title")
                .content("Original Content")
                .category(PostCategory.NOTICE)
                .writer("Writer")
                .mail("example@mail.com")
                .password("password123")
                .build();
        postRepository.saveAndFlush(existingPost);
    }

    @Test
    @DisplayName("수정 요청에서 null 값이 들어오면 기존 필드가 유지된다")
    void testUpdatePost_NullFieldsRemainUnchanged() {
        // Given
        UpdatePostRequest request = new UpdatePostRequest(
                null, // Title is null
                null, // Content is null
                null, // Category is null
                "password123" // Valid password
        );

        // When
        var response = postService.updatePost(existingPost.getId(), request);

        // Then
        assertEquals("Original Title", response.getTitle(), "Title should remain unchanged");
        assertEquals("Original Content", response.getContent(), "Content should remain unchanged");
        assertEquals(PostCategory.NOTICE, response.getCategory(), "Category should remain unchanged");
    }

    @Test
    @DisplayName("수정 요청에서 일부 필드만 수정된다")
    void testUpdatePost_PartialUpdate() {
        // Given
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title", // New title
                null,            // Content is null
                null,            // Category is null
                "password123"    // Valid password
        );

        // When
        var response = postService.updatePost(existingPost.getId(), request);

        // Then
        assertEquals("Updated Title", response.getTitle(), "Title should be updated");
        assertEquals("Original Content", response.getContent(), "Content should remain unchanged");
        assertEquals(PostCategory.NOTICE, response.getCategory(), "Category should remain unchanged");
    }

    @Test
    @DisplayName("수정 요청 시 잘못된 비밀번호로 수정 실패")
    void testUpdatePost_InvalidPassword() {
        // Given
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Content",
                PostCategory.REVIEW,
                "wrongPassword"
        );

        // When & Then
        assertThrows(InvalidPasswordException.class, () -> {
            postService.updatePost(existingPost.getId(), request);
        }, "InvalidPasswordException should be thrown for incorrect password");
    }

    @Test
    @DisplayName("수정 시 updatedAt이 변경된다")
    void testUpdatePost_UpdatedAtChanges() throws InterruptedException {
        // Given
        LocalDateTime beforeUpdate = existingPost.getUpdatedAt();

        Thread.sleep(1000);
        UpdatePostRequest request = new UpdatePostRequest("New Title", "New Content", PostCategory.QNA, "password123");

        // When
        postService.updatePost(existingPost.getId(), request);
        postRepository.flush();
        Post updatedPost = postRepository.findById(existingPost.getId()).orElseThrow();

        // Then
        assertNotNull(updatedPost.getUpdatedAt(), "updatedAt은 null이 아니어야 합니다.");
        assertNotEquals(beforeUpdate, updatedPost.getUpdatedAt(), "updatedAt이 업데이트되어야 합니다.");
        assertEquals("New Title", updatedPost.getTitle(), "제목이 업데이트되어야 합니다.");
        assertEquals("New Content", updatedPost.getContent(), "내용이 업데이트되어야 합니다.");
        assertEquals(PostCategory.QNA, updatedPost.getCategory(), "카테고리가 업데이트되어야 합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정 시 PostNotFound 예외 발생")
    void testUpdatePost_NotFound() {
        // Given
        Long invalidPostId = 999L;
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Content",
                null,
                "password123"
        );

        // When & Then
        assertThrows(PostNotFound.class, () -> {
            postService.updatePost(invalidPostId, request);
        }, "PostNotFound 예외가 발생해야 한다");
    }
}