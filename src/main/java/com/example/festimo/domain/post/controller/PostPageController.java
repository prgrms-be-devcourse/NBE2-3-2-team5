package com.example.festimo.domain.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostPageController {

    @GetMapping("/community/**")
    public String community() {
        return "index";
    }
}