package com.example.festimo.post;

import com.example.festimo.domain.post.dto.PostRequestDto;
import com.example.festimo.domain.post.dto.PostResponseDto;
import com.example.festimo.domain.post.entity.PostCategory;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.post.service.PostService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class CreatePostTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("게시글 등록")
    @Rollback(value = false)
    void posting() {
        // Given
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitle("테스트 게시글 제목");
        postRequestDto.setWriter("작성자1");
        postRequestDto.setMail("test@example.com");
        postRequestDto.setPassword("1234");
        postRequestDto.setContent("테스트 내용입니다.");
        postRequestDto.setCategory(PostCategory.COMPANION);

        // When
        PostResponseDto responseDto = postService.createPost(postRequestDto);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo(postRequestDto.getTitle());
        assertThat(responseDto.getWriter()).isEqualTo(postRequestDto.getWriter());
        assertThat(responseDto.getContent()).isEqualTo(postRequestDto.getContent());

        System.out.println("등록된 게시글 ID: " + responseDto.getId());
    }

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("필수 입력값 누락으로 게시글 등록 실패")
    void failToPostWhenMissingRequiredFields() {
        // Given
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitle(null); // 제목 누락
        postRequestDto.setWriter("작성자1");
        postRequestDto.setMail("test@example.com");
        postRequestDto.setPassword("1234");
        postRequestDto.setContent("테스트 내용입니다.");
        postRequestDto.setCategory(PostCategory.QNA);

        // When
        Set<ConstraintViolation<PostRequestDto>> violations = validator.validate(postRequestDto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("제목은 필수 입력 항목입니다."));
    }

    @Test
    @DisplayName("제목 길이 초과로 게시글 등록 실패")
    void failToPostWhenTitleExceedsLengthLimit() {
        // Given
        String longTitle = "a".repeat(51);
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitle(longTitle);
        postRequestDto.setWriter("작성자1");
        postRequestDto.setMail("test@example.com");
        postRequestDto.setPassword("1234");
        postRequestDto.setContent("테스트 내용입니다.");
        postRequestDto.setCategory(PostCategory.NOTICE);

        // When & Then
        assertThatThrownBy(() -> postService.createPost(postRequestDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("제목은 50자 이하로 입력해주세요.");
    }

    @Test
    @DisplayName("비밀번호 형식이 맞지 않아 게시글 등록 실패")
    void failToPostWhenPasswordInvalid() {
        // Given
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitle("테스트 게시글 제목");
        postRequestDto.setWriter("작성자1");
        postRequestDto.setMail("test@example.com");
        postRequestDto.setPassword("abc");
        postRequestDto.setContent("테스트 내용입니다.");
        postRequestDto.setCategory(PostCategory.QNA);

        // When & Then
        assertThatThrownBy(() -> postService.createPost(postRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 최소 4자 이상이어야 합니다.");
    }
}
