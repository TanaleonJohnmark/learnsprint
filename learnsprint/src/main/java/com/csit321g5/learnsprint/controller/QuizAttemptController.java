package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.QuizAttempt;
import com.csit321g5.learnsprint.service.QuizAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping
    public ResponseEntity<QuizAttempt> create(@RequestBody QuizAttempt quizAttempt) {
        return new ResponseEntity<>(quizAttemptService.create(quizAttempt), HttpStatus.CREATED);
    }

    @GetMapping
    public List<QuizAttempt> findAll() {
        return quizAttemptService.findAll();
    }

    @GetMapping("/{id}")
    public QuizAttempt findById(@PathVariable Long id) {
        return quizAttemptService.findById(id);
    }

    @PutMapping("/{id}")
    public QuizAttempt update(@PathVariable Long id, @RequestBody QuizAttempt quizAttempt) {
        return quizAttemptService.update(id, quizAttempt);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        quizAttemptService.delete(id);
        return Map.of("message", "QuizAttempt " + id + " deleted.");
    }
}
