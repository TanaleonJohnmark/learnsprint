package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.Admin;
import com.csit321g5.learnsprint.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<Admin> create(@RequestBody Admin admin) {
        return new ResponseEntity<>(adminService.create(admin), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Admin> findAll() {
        return adminService.findAll();
    }

    @GetMapping("/{id}")
    public Admin findById(@PathVariable Long id) {
        return adminService.findById(id);
    }

    @PutMapping("/{id}")
    public Admin update(@PathVariable Long id, @RequestBody Admin admin) {
        return adminService.update(id, admin);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        adminService.delete(id);
        return Map.of("message", "Admin " + id + " deleted.");
    }
}
