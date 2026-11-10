package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Course;
import com.csit321g5.learnsprint.entity.Track;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.CourseRepository;
import com.csit321g5.learnsprint.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final CourseRepository courseRepository;

    public TrackService(TrackRepository trackRepository,
                        CourseRepository courseRepository) {
        this.trackRepository = trackRepository;
        this.courseRepository = courseRepository;
    }

    /** CREATE */
    public Track create(Track track) {
        validate(track);
        track.setTrackId(null);
        track.setCourse(findCourse(track.getCourse()));
        return trackRepository.save(track);
    }

    /** READ all */
    public List<Track> findAll() {
        return trackRepository.findAll();
    }

    /** READ one */
    public Track findById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track", id));
    }

    /** UPDATE */
    public Track update(Long id, Track changes) {
        Track existing = findById(id);
        validate(changes);
        existing.setTitle(changes.getTitle());
        existing.setSequenceNo(changes.getSequenceNo());
        existing.setCourse(findCourse(changes.getCourse()));
        return trackRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!trackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Track", id);
        }
        trackRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Track track) {
        if (isBlank(track.getTitle())) {
            throw new BadRequestException("Title is required.");
        }
        if (track.getCourse() == null || track.getCourse().getCourseId() == null) {
            throw new BadRequestException("Course is required.");
        }
    }

    /** Swaps the id sent by the client for the real Course row. */
    private Course findCourse(Course reference) {
        Long id = reference.getCourseId();
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
