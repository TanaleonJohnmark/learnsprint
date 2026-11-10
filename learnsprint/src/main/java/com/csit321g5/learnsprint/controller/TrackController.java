package com.csit321g5.learnsprint.controller;

import com.csit321g5.learnsprint.entity.Track;
import com.csit321g5.learnsprint.service.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping
    public ResponseEntity<Track> create(@RequestBody Track track) {
        return new ResponseEntity<>(trackService.create(track), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Track> findAll() {
        return trackService.findAll();
    }

    @GetMapping("/{id}")
    public Track findById(@PathVariable Long id) {
        return trackService.findById(id);
    }

    @PutMapping("/{id}")
    public Track update(@PathVariable Long id, @RequestBody Track track) {
        return trackService.update(id, track);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        trackService.delete(id);
        return Map.of("message", "Track " + id + " deleted.");
    }
}
