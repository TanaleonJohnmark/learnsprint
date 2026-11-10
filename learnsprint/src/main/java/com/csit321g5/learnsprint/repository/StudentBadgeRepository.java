package com.csit321g5.learnsprint.repository;

import com.csit321g5.learnsprint.entity.StudentBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentBadgeRepository extends JpaRepository<StudentBadge, Long> {
}
