package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Lesson;
import com.csit321g5.learnsprint.entity.LessonProgress;
import com.csit321g5.learnsprint.entity.Student;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.LessonProgressRepository;
import com.csit321g5.learnsprint.repository.LessonRepository;
import com.csit321g5.learnsprint.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;

    public LessonProgressService(LessonProgressRepository lessonProgressRepository,
                                 StudentRepository studentRepository,
                                 LessonRepository lessonRepository) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.studentRepository = studentRepository;
        this.lessonRepository = lessonRepository;
    }

    /** CREATE */
    public LessonProgress create(LessonProgress lessonProgress) {
        validate(lessonProgress);
        lessonProgress.setProgressId(null);
        if (isBlank(lessonProgress.getStatus())) {
            lessonProgress.setStatus("IN_PROGRESS");
        }
        if (lessonProgress.getIsSynced() == null) {
            lessonProgress.setIsSynced(false);
        }
        lessonProgress.setStudent(findStudent(lessonProgress.getStudent()));
        lessonProgress.setLesson(findLesson(lessonProgress.getLesson()));
        return lessonProgressRepository.save(lessonProgress);
    }

    /** READ all */
    public List<LessonProgress> findAll() {
        return lessonProgressRepository.findAll();
    }

    /** READ one */
    public LessonProgress findById(Long id) {
        return lessonProgressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LessonProgress", id));
    }

    /** UPDATE */
    public LessonProgress update(Long id, LessonProgress changes) {
        LessonProgress existing = findById(id);
        validate(changes);
        existing.setStatus(changes.getStatus());
        existing.setDateCompleted(changes.getDateCompleted());
        existing.setIsSynced(changes.getIsSynced());
        existing.setStudent(findStudent(changes.getStudent()));
        existing.setLesson(findLesson(changes.getLesson()));
        return lessonProgressRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!lessonProgressRepository.existsById(id)) {
            throw new ResourceNotFoundException("LessonProgress", id);
        }
        lessonProgressRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(LessonProgress lessonProgress) {
        if (lessonProgress.getStudent() == null || lessonProgress.getStudent().getUserId() == null) {
            throw new BadRequestException("Student is required.");
        }
        if (lessonProgress.getLesson() == null || lessonProgress.getLesson().getLessonId() == null) {
            throw new BadRequestException("Lesson is required.");
        }
    }

    /** Swaps the id sent by the client for the real Student row. */
    private Student findStudent(Student reference) {
        Long id = reference.getUserId();
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    /** Swaps the id sent by the client for the real Lesson row. */
    private Lesson findLesson(Lesson reference) {
        Long id = reference.getLessonId();
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", id));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
