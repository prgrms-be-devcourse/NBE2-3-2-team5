package com.example.festimo.admin.controller;


import com.example.festimo.admin.dto.AdminUpdateUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.festimo.admin.dto.AdminDTO;
import com.example.festimo.admin.service.AdminService;


@RestController
@RequestMapping("/adi/admin")
@Tag(name = "관리자 API", description = "관리자가 회원 정보를 관리하는 API")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "관리자의 회원 조회", description = "모든 회원 정보")
    public ResponseEntity<Page<AdminDTO>> getAllUsers(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size) {
        Page<AdminDTO> users = adminService.getAllUsers(page,size);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userid}")
    @Operation(summary = "관리자의 회원 수정", description = "회원 정보 수정")
    public ResponseEntity<AdminDTO> updateUser(
            @PathVariable Long userid,
            @Valid @RequestBody AdminUpdateUserDTO adminUpdateUserDTO) {
        AdminDTO user = adminService.updateUser(userid, adminUpdateUserDTO);
        return ResponseEntity.ok(user);

    }

    @DeleteMapping("/{userid}")
    @Operation(summary = "관리자의 회원 삭제", description = "회원 정보 삭제")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userid){
        adminService.deleteUser(userid);
        return ResponseEntity.noContent().build();
    }

}
