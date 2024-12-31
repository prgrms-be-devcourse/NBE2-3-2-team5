package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.TagResponse;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final PostRepository postRepository;

    public List<TagResponse> getTopWeeklyTags() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Post> recentPosts = postRepository.findByCreatedAtAfter(oneWeekAgo);

        Map<String, Long> tagCount = new HashMap<>();
        for (Post post : recentPosts) {
            if (post.getTags() != null && !post.getTags().isEmpty()) {
                for (String tag : post.getTags()) {
                    tag = tag.trim();
                    tagCount.merge(tag, 1L, Long::sum);
                }
            }
        }

        return tagCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new TagResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}