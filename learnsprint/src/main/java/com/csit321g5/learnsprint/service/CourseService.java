package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Course;
import com.csit321g5.learnsprint.entity.Faculty;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.CourseRepository;
import com.csit321g5.learnsprint.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;

    public CourseService(CourseRepository courseRepository,
                         FacultyRepository facultyRepository) {
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
    }

    /** CREATE */
    public Course create(Course course) {
        validate(course);
        course.setCourseId(null);
        course.setFaculty(findFaculty(course.getFaculty()));
        return courseRepository.save(course);
    }

    /** READ all */
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    /** READ one */
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    /** UPDATE */
    public Course update(Long id, Course changes) {
        Course existing = findById(id);
        validate(changes);
        existing.setCourseCode(changes.getCourseCode());
        existing.setTitle(changes.getTitle());
        existing.setDescription(changes.getDescription());
        existing.setFaculty(findFaculty(changes.getFaculty()));
        return courseRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", id);
        }
        courseRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Course course) {
        if (isBlank(course.getCourseCode())) {
            throw new BadRequestException("Course code is required.");
        }
        if (isBlank(course.getTitle())) {
            throw new BadRequestException("Title is required.");
        }
        if (course.getFaculty() == null || course.getFaculty().getUserId() == null) {
            throw new BadRequestException("Faculty is required.");
        }
    }

    /** Swaps the id sent by the client for the real Faculty row. */
    private Faculty findFaculty(Faculty reference) {
        Long id = reference.getUserId();
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", id));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
