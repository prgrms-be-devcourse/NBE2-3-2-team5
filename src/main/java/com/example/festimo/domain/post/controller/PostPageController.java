package com.example.festimo.domain.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostPageController {

    @GetMapping(value = {"/", "/community/**", "/login", "/register"})
    public String forward() {
        return "forward:/index.html";
    }
}