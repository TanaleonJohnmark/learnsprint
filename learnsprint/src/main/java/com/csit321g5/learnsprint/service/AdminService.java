package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Admin;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /** CREATE */
    public Admin create(Admin admin) {
        validate(admin);
        if (isBlank(admin.getPassword())) {
            throw new BadRequestException("Password is required.");
        }
        admin.setUserId(null);
        return adminRepository.save(admin);
    }

    /** READ all */
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    /** READ one */
    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", id));
    }

    /** UPDATE */
    public Admin update(Long id, Admin changes) {
        Admin existing = findById(id);
        validate(changes);
        existing.setFname(changes.getFname());
        existing.setLname(changes.getLname());
        existing.setEmail(changes.getEmail());
        if (!isBlank(changes.getPassword())) {
            existing.setPassword(changes.getPassword());
        }
        existing.setAdminLevel(changes.getAdminLevel());
        return adminRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin", id);
        }
        adminRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Admin admin) {
        if (isBlank(admin.getFname())) {
            throw new BadRequestException("First name is required.");
        }
        if (isBlank(admin.getLname())) {
            throw new BadRequestException("Last name is required.");
        }
        if (isBlank(admin.getEmail())) {
            throw new BadRequestException("Email is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
