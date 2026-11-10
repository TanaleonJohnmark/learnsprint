package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.repository.*;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/** Counts the rows of every table for the overview page of the React app. */
@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    private final TrackRepository trackRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;

    public DashboardService(StudentRepository studentRepository,
                            FacultyRepository facultyRepository,
                            AdminRepository adminRepository,
                            CourseRepository courseRepository,
                            TrackRepository trackRepository,
                            LessonRepository lessonRepository,
                            QuizRepository quizRepository,
                            QuestionRepository questionRepository,
                            EnrollmentRepository enrollmentRepository,
                            LessonProgressRepository lessonProgressRepository,
                            QuizAttemptRepository quizAttemptRepository,
                            BadgeRepository badgeRepository,
                            StudentBadgeRepository studentBadgeRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.adminRepository = adminRepository;
        this.courseRepository = courseRepository;
        this.trackRepository = trackRepository;
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.badgeRepository = badgeRepository;
        this.studentBadgeRepository = studentBadgeRepository;
    }

    public Map<String, Long> counts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("students", studentRepository.count());
        counts.put("faculty", facultyRepository.count());
        counts.put("admins", adminRepository.count());
        counts.put("courses", courseRepository.count());
        counts.put("tracks", trackRepository.count());
        counts.put("lessons", lessonRepository.count());
        counts.put("quizzes", quizRepository.count());
        counts.put("questions", questionRepository.count());
        counts.put("enrollments", enrollmentRepository.count());
        counts.put("lessonProgress", lessonProgressRepository.count());
        counts.put("quizAttempts", quizAttemptRepository.count());
        counts.put("badges", badgeRepository.count());
        counts.put("studentBadges", studentBadgeRepository.count());
        return counts;
    }
}
