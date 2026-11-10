package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.StudentBadge;
import com.csit321g5.learnsprint.service.StudentBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student-badges")
public class StudentBadgeController {

    private final StudentBadgeService studentBadgeService;

    public StudentBadgeController(StudentBadgeService studentBadgeService) {
        this.studentBadgeService = studentBadgeService;
    }

    @PostMapping
    public ResponseEntity<StudentBadge> create(@RequestBody StudentBadge studentBadge) {
        return new ResponseEntity<>(studentBadgeService.create(studentBadge), HttpStatus.CREATED);
    }

    @GetMapping
    public List<StudentBadge> findAll() {
        return studentBadgeService.findAll();
    }

    @GetMapping("/{id}")
    public StudentBadge findById(@PathVariable Long id) {
        return studentBadgeService.findById(id);
    }

    @PutMapping("/{id}")
    public StudentBadge update(@PathVariable Long id, @RequestBody StudentBadge studentBadge) {
        return studentBadgeService.update(id, studentBadge);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        studentBadgeService.delete(id);
        return Map.of("message", "StudentBadge " + id + " deleted.");
    }
}
