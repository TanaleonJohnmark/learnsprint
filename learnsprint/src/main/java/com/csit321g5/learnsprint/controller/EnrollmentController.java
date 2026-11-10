package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.Enrollment;
import com.csit321g5.learnsprint.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<Enrollment> create(@RequestBody Enrollment enrollment) {
        return new ResponseEntity<>(enrollmentService.create(enrollment), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Enrollment> findAll() {
        return enrollmentService.findAll();
    }

    @GetMapping("/{id}")
    public Enrollment findById(@PathVariable Long id) {
        return enrollmentService.findById(id);
    }

    @PutMapping("/{id}")
    public Enrollment update(@PathVariable Long id, @RequestBody Enrollment enrollment) {
        return enrollmentService.update(id, enrollment);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        enrollmentService.delete(id);
        return Map.of("message", "Enrollment " + id + " deleted.");
    }
}
