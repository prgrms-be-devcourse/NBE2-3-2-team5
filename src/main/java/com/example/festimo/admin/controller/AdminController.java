package com.example.festimo.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<List<AdminDTO>> getAll() {
        List<AdminDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
