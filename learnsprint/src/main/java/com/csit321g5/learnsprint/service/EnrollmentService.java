package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Course;
import com.csit321g5.learnsprint.entity.Enrollment;
import com.csit321g5.learnsprint.entity.Student;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.CourseRepository;
import com.csit321g5.learnsprint.repository.EnrollmentRepository;
import com.csit321g5.learnsprint.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    /** CREATE */
    public Enrollment create(Enrollment enrollment) {
        validate(enrollment);
        enrollment.setEnrollmentId(null);
        if (enrollment.getDateEnrolled() == null) {
            enrollment.setDateEnrolled(LocalDate.now());
        }
        enrollment.setStudent(findStudent(enrollment.getStudent()));
        enrollment.setCourse(findCourse(enrollment.getCourse()));
        return enrollmentRepository.save(enrollment);
    }

    /** READ all */
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    /** READ one */
    public Enrollment findById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", id));
    }

    /** UPDATE */
    public Enrollment update(Long id, Enrollment changes) {
        Enrollment existing = findById(id);
        validate(changes);
        existing.setDateEnrolled(changes.getDateEnrolled());
        existing.setStudent(findStudent(changes.getStudent()));
        existing.setCourse(findCourse(changes.getCourse()));
        return enrollmentRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrollment", id);
        }
        enrollmentRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Enrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getStudent().getUserId() == null) {
            throw new BadRequestException("Student is required.");
        }
        if (enrollment.getCourse() == null || enrollment.getCourse().getCourseId() == null) {
            throw new BadRequestException("Course is required.");
        }
    }

    /** Swaps the id sent by the client for the real Student row. */
    private Student findStudent(Student reference) {
        Long id = reference.getUserId();
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    /** Swaps the id sent by the client for the real Course row. */
    private Course findCourse(Course reference) {
        Long id = reference.getCourseId();
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }
}
