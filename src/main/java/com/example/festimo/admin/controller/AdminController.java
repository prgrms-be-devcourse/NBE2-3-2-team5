package com.example.festimo.admin.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.festimo.admin.dto.AdminDTO;
import com.example.festimo.admin.service.AdminService;


@RestController
@RequestMapping("/adi/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AdminDTO>> getAllUsers(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size) {
        Page<AdminDTO> users = adminService.getAllUsers(page,size);
        return ResponseEntity.ok(users);
    }
}
