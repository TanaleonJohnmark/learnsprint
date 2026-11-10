package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Badge;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.BadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    /** CREATE */
    public Badge create(Badge badge) {
        validate(badge);
        badge.setBadgeId(null);
        return badgeRepository.save(badge);
    }

    /** READ all */
    public List<Badge> findAll() {
        return badgeRepository.findAll();
    }

    /** READ one */
    public Badge findById(Long id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge", id));
    }

    /** UPDATE */
    public Badge update(Long id, Badge changes) {
        Badge existing = findById(id);
        validate(changes);
        existing.setName(changes.getName());
        existing.setDescription(changes.getDescription());
        existing.setCriteria(changes.getCriteria());
        return badgeRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!badgeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Badge", id);
        }
        badgeRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Badge badge) {
        if (isBlank(badge.getName())) {
            throw new BadRequestException("Name is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
