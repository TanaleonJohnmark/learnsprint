package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Lesson;
import com.csit321g5.learnsprint.entity.Quiz;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.LessonRepository;
import com.csit321g5.learnsprint.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;

    public QuizService(QuizRepository quizRepository,
                       LessonRepository lessonRepository) {
        this.quizRepository = quizRepository;
        this.lessonRepository = lessonRepository;
    }

    /** CREATE */
    public Quiz create(Quiz quiz) {
        validate(quiz);
        quiz.setQuizId(null);
        quiz.setLesson(findLesson(quiz.getLesson()));
        return quizRepository.save(quiz);
    }

    /** READ all */
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    /** READ one */
    public Quiz findById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }

    /** UPDATE */
    public Quiz update(Long id, Quiz changes) {
        Quiz existing = findById(id);
        validate(changes);
        existing.setTitle(changes.getTitle());
        existing.setPassingScore(changes.getPassingScore());
        existing.setLesson(findLesson(changes.getLesson()));
        return quizRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", id);
        }
        quizRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Quiz quiz) {
        if (isBlank(quiz.getTitle())) {
            throw new BadRequestException("Title is required.");
        }
        if (quiz.getLesson() == null || quiz.getLesson().getLessonId() == null) {
            throw new BadRequestException("Lesson is required.");
        }
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
