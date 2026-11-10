package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.Lesson;
import com.csit321g5.learnsprint.service.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<Lesson> create(@RequestBody Lesson lesson) {
        return new ResponseEntity<>(lessonService.create(lesson), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Lesson> findAll() {
        return lessonService.findAll();
    }

    @GetMapping("/{id}")
    public Lesson findById(@PathVariable Long id) {
        return lessonService.findById(id);
    }

    @PutMapping("/{id}")
    public Lesson update(@PathVariable Long id, @RequestBody Lesson lesson) {
        return lessonService.update(id, lesson);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return Map.of("message", "Lesson " + id + " deleted.");
    }
}
