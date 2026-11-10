package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Student;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /** CREATE */
    public Student create(Student student) {
        validate(student);
        if (isBlank(student.getPassword())) {
            throw new BadRequestException("Password is required.");
        }
        student.setUserId(null);
        if (student.getTotalPoints() == null) {
            student.setTotalPoints(0);
        }
        if (student.getCurrentStreak() == null) {
            student.setCurrentStreak(0);
        }
        return studentRepository.save(student);
    }

    /** READ all */
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    /** READ one */
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    /** UPDATE */
    public Student update(Long id, Student changes) {
        Student existing = findById(id);
        validate(changes);
        existing.setFname(changes.getFname());
        existing.setLname(changes.getLname());
        existing.setEmail(changes.getEmail());
        if (!isBlank(changes.getPassword())) {
            existing.setPassword(changes.getPassword());
        }
        existing.setSchoolId(changes.getSchoolId());
        existing.setProgram(changes.getProgram());
        existing.setYearLevel(changes.getYearLevel());
        existing.setTotalPoints(changes.getTotalPoints());
        existing.setCurrentStreak(changes.getCurrentStreak());
        return studentRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", id);
        }
        studentRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Student student) {
        if (isBlank(student.getFname())) {
            throw new BadRequestException("First name is required.");
        }
        if (isBlank(student.getLname())) {
            throw new BadRequestException("Last name is required.");
        }
        if (isBlank(student.getEmail())) {
            throw new BadRequestException("Email is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
