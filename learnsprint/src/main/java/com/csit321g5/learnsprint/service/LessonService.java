package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Lesson;
import com.csit321g5.learnsprint.entity.Track;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.LessonRepository;
import com.csit321g5.learnsprint.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final TrackRepository trackRepository;

    public LessonService(LessonRepository lessonRepository,
                         TrackRepository trackRepository) {
        this.lessonRepository = lessonRepository;
        this.trackRepository = trackRepository;
    }

    /** CREATE */
    public Lesson create(Lesson lesson) {
        validate(lesson);
        lesson.setLessonId(null);
        lesson.setTrack(findTrack(lesson.getTrack()));
        return lessonRepository.save(lesson);
    }

    /** READ all */
    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    /** READ one */
    public Lesson findById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", id));
    }

    /** UPDATE */
    public Lesson update(Long id, Lesson changes) {
        Lesson existing = findById(id);
        validate(changes);
        existing.setTitle(changes.getTitle());
        existing.setContent(changes.getContent());
        existing.setDurationMin(changes.getDurationMin());
        existing.setSequenceNo(changes.getSequenceNo());
        existing.setTrack(findTrack(changes.getTrack()));
        return lessonRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson", id);
        }
        lessonRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Lesson lesson) {
        if (isBlank(lesson.getTitle())) {
            throw new BadRequestException("Title is required.");
        }
        if (lesson.getTrack() == null || lesson.getTrack().getTrackId() == null) {
            throw new BadRequestException("Track is required.");
        }
    }

    /** Swaps the id sent by the client for the real Track row. */
    private Track findTrack(Track reference) {
        Long id = reference.getTrackId();
        return trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track", id));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
