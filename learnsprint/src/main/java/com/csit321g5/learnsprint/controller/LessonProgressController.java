package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.LessonProgress;
import com.csit321g5.learnsprint.service.LessonProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lesson-progress")
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    public LessonProgressController(LessonProgressService lessonProgressService) {
        this.lessonProgressService = lessonProgressService;
    }

    @PostMapping
    public ResponseEntity<LessonProgress> create(@RequestBody LessonProgress lessonProgress) {
        return new ResponseEntity<>(lessonProgressService.create(lessonProgress), HttpStatus.CREATED);
    }

    @GetMapping
    public List<LessonProgress> findAll() {
        return lessonProgressService.findAll();
    }

    @GetMapping("/{id}")
    public LessonProgress findById(@PathVariable Long id) {
        return lessonProgressService.findById(id);
    }

    @PutMapping("/{id}")
    public LessonProgress update(@PathVariable Long id, @RequestBody LessonProgress lessonProgress) {
        return lessonProgressService.update(id, lessonProgress);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        lessonProgressService.delete(id);
        return Map.of("message", "LessonProgress " + id + " deleted.");
    }
}
