package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.Faculty;
import com.csit321g5.learnsprint.service.FacultyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<Faculty> create(@RequestBody Faculty faculty) {
        return new ResponseEntity<>(facultyService.create(faculty), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Faculty> findAll() {
        return facultyService.findAll();
    }

    @GetMapping("/{id}")
    public Faculty findById(@PathVariable Long id) {
        return facultyService.findById(id);
    }

    @PutMapping("/{id}")
    public Faculty update(@PathVariable Long id, @RequestBody Faculty faculty) {
        return facultyService.update(id, faculty);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return Map.of("message", "Faculty " + id + " deleted.");
    }
}
