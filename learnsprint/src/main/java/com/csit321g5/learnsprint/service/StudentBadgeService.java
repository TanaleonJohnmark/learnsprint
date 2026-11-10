package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Badge;
import com.csit321g5.learnsprint.entity.Student;
import com.csit321g5.learnsprint.entity.StudentBadge;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.BadgeRepository;
import com.csit321g5.learnsprint.repository.StudentBadgeRepository;
import com.csit321g5.learnsprint.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentBadgeService {

    private final StudentBadgeRepository studentBadgeRepository;
    private final StudentRepository studentRepository;
    private final BadgeRepository badgeRepository;

    public StudentBadgeService(StudentBadgeRepository studentBadgeRepository,
                               StudentRepository studentRepository,
                               BadgeRepository badgeRepository) {
        this.studentBadgeRepository = studentBadgeRepository;
        this.studentRepository = studentRepository;
        this.badgeRepository = badgeRepository;
    }

    /** CREATE */
    public StudentBadge create(StudentBadge studentBadge) {
        validate(studentBadge);
        studentBadge.setStudentBadgeId(null);
        if (studentBadge.getDateEarned() == null) {
            studentBadge.setDateEarned(LocalDate.now());
        }
        studentBadge.setStudent(findStudent(studentBadge.getStudent()));
        studentBadge.setBadge(findBadge(studentBadge.getBadge()));
        return studentBadgeRepository.save(studentBadge);
    }

    /** READ all */
    public List<StudentBadge> findAll() {
        return studentBadgeRepository.findAll();
    }

    /** READ one */
    public StudentBadge findById(Long id) {
        return studentBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentBadge", id));
    }

    /** UPDATE */
    public StudentBadge update(Long id, StudentBadge changes) {
        StudentBadge existing = findById(id);
        validate(changes);
        existing.setDateEarned(changes.getDateEarned());
        existing.setVerificationCode(changes.getVerificationCode());
        existing.setStudent(findStudent(changes.getStudent()));
        existing.setBadge(findBadge(changes.getBadge()));
        return studentBadgeRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!studentBadgeRepository.existsById(id)) {
            throw new ResourceNotFoundException("StudentBadge", id);
        }
        studentBadgeRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(StudentBadge studentBadge) {
        if (studentBadge.getStudent() == null || studentBadge.getStudent().getUserId() == null) {
            throw new BadRequestException("Student is required.");
        }
        if (studentBadge.getBadge() == null || studentBadge.getBadge().getBadgeId() == null) {
            throw new BadRequestException("Badge is required.");
        }
    }

    /** Swaps the id sent by the client for the real Student row. */
    private Student findStudent(Student reference) {
        Long id = reference.getUserId();
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    /** Swaps the id sent by the client for the real Badge row. */
    private Badge findBadge(Badge reference) {
        Long id = reference.getBadgeId();
        return badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge", id));
    }
}
