package com.example.festimo.post;

import com.example.festimo.domain.post.entity.Comment;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.CommentRepository;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.post.service.PostServiceImpl;
import com.example.festimo.exception.CommentNotFound;
import com.example.festimo.exception.PostNotFound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateDeleteCommentTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    @DisplayName("댓글이 존재하면 삭제")
    void deleteComment_Success() {
        // Given
        Long postId = 1L;
        Integer sequence = 1;

        Post post = mock(Post.class);
        Comment comment = Comment.builder()
                .id(1L)
                .sequence(sequence)
                .post(post)
                .comment("Test comment")
                .nickname("hayeon")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndSequence(postId, sequence))
                .thenReturn(Optional.of(comment));

        // When
        postService.deleteComment(postId, sequence);

        // Then
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName(" 존재하지 않는 게시글")
    void deleteComment_Fail_PostNotFound() {
        // Given
        Long postId = 1L;
        Integer sequence = 1;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.deleteComment(postId, sequence))
                .isInstanceOf(PostNotFound.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다.");

        verify(commentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("존재하지 않는 댓글")
    void deleteComment_Fail_CommentNotFound() {
        // Given
        Long postId = 1L;
        Integer sequence = 1;

        Post post = mock(Post.class);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndSequence(postId, sequence)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.deleteComment(postId, sequence))
                .isInstanceOf(CommentNotFound.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다.");

        verify(commentRepository, never()).delete(any());
    }
}