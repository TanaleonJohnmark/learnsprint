package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.Badge;
import com.csit321g5.learnsprint.service.BadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping
    public ResponseEntity<Badge> create(@RequestBody Badge badge) {
        return new ResponseEntity<>(badgeService.create(badge), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Badge> findAll() {
        return badgeService.findAll();
    }

    @GetMapping("/{id}")
    public Badge findById(@PathVariable Long id) {
        return badgeService.findById(id);
    }

    @PutMapping("/{id}")
    public Badge update(@PathVariable Long id, @RequestBody Badge badge) {
        return badgeService.update(id, badge);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        badgeService.delete(id);
        return Map.of("message", "Badge " + id + " deleted.");
    }
}
