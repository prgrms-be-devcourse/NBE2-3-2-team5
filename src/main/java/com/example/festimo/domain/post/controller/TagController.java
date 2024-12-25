package com.example.festimo.domain.post.controller;

import com.example.festimo.domain.post.dto.TagResponse;
import com.example.festimo.domain.post.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/weekly-top")
    public ResponseEntity<List<TagResponse>> getWeeklyTopTags() {
        List<TagResponse> topTags = tagService.getTopWeeklyTags();
        return ResponseEntity.ok(topTags);
    }
}