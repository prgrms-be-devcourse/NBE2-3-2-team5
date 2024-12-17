package com.example.festimo.post;

import com.example.festimo.domain.post.controller.PostController;
import com.example.festimo.domain.post.dto.PostResponse;
import com.example.festimo.domain.post.service.PostService;
import com.example.festimo.global.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.example.festimo.domain.post.entity.PostCategory.COMPANION;
import static com.example.festimo.domain.post.entity.PostCategory.REVIEW;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GetPostTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    @DisplayName("전체 게시글 조회")
    public void testGetAllPosts() throws Exception {
        List<PostResponse> postResponses = List.of(
                new PostResponse(1L, "Title 1", "Writer 1", "mail1@example.com", "Content 1", COMPANION),
                new PostResponse(2L, "Title 2", "Writer 2", "mail2@example.com", "Content 2", REVIEW)
        );

        PageResponse<PostResponse> pageResponse = new PageResponse<>(
                new PageImpl<>(postResponses, PageRequest.of(0, 10), 20)
        );

        Mockito.when(postService.getAllPosts(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/companions?page=1&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Title 1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Title 2"))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(20))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }
}