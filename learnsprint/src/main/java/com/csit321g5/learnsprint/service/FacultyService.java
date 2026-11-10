package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Faculty;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    /** CREATE */
    public Faculty create(Faculty faculty) {
        validate(faculty);
        if (isBlank(faculty.getPassword())) {
            throw new BadRequestException("Password is required.");
        }
        faculty.setUserId(null);
        return facultyRepository.save(faculty);
    }

    /** READ all */
    public List<Faculty> findAll() {
        return facultyRepository.findAll();
    }

    /** READ one */
    public Faculty findById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", id));
    }

    /** UPDATE */
    public Faculty update(Long id, Faculty changes) {
        Faculty existing = findById(id);
        validate(changes);
        existing.setFname(changes.getFname());
        existing.setLname(changes.getLname());
        existing.setEmail(changes.getEmail());
        if (!isBlank(changes.getPassword())) {
            existing.setPassword(changes.getPassword());
        }
        existing.setEmployeeNo(changes.getEmployeeNo());
        existing.setDepartment(changes.getDepartment());
        return facultyRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Faculty", id);
        }
        facultyRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Faculty faculty) {
        if (isBlank(faculty.getFname())) {
            throw new BadRequestException("First name is required.");
        }
        if (isBlank(faculty.getLname())) {
            throw new BadRequestException("Last name is required.");
        }
        if (isBlank(faculty.getEmail())) {
            throw new BadRequestException("Email is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
