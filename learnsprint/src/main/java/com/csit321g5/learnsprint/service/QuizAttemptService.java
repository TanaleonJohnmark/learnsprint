package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Quiz;
import com.csit321g5.learnsprint.entity.QuizAttempt;
import com.csit321g5.learnsprint.entity.Student;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.QuizAttemptRepository;
import com.csit321g5.learnsprint.repository.QuizRepository;
import com.csit321g5.learnsprint.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final StudentRepository studentRepository;
    private final QuizRepository quizRepository;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository,
                              StudentRepository studentRepository,
                              QuizRepository quizRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.studentRepository = studentRepository;
        this.quizRepository = quizRepository;
    }

    /** CREATE */
    public QuizAttempt create(QuizAttempt quizAttempt) {
        validate(quizAttempt);
        quizAttempt.setAttemptId(null);
        if (quizAttempt.getDateTaken() == null) {
            quizAttempt.setDateTaken(LocalDate.now());
        }
        if (quizAttempt.getIsSynced() == null) {
            quizAttempt.setIsSynced(false);
        }
        quizAttempt.setStudent(findStudent(quizAttempt.getStudent()));
        quizAttempt.setQuiz(findQuiz(quizAttempt.getQuiz()));
        return quizAttemptRepository.save(quizAttempt);
    }

    /** READ all */
    public List<QuizAttempt> findAll() {
        return quizAttemptRepository.findAll();
    }

    /** READ one */
    public QuizAttempt findById(Long id) {
        return quizAttemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt", id));
    }

    /** UPDATE */
    public QuizAttempt update(Long id, QuizAttempt changes) {
        QuizAttempt existing = findById(id);
        validate(changes);
        existing.setScore(changes.getScore());
        existing.setDateTaken(changes.getDateTaken());
        existing.setIsSynced(changes.getIsSynced());
        existing.setStudent(findStudent(changes.getStudent()));
        existing.setQuiz(findQuiz(changes.getQuiz()));
        return quizAttemptRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!quizAttemptRepository.existsById(id)) {
            throw new ResourceNotFoundException("QuizAttempt", id);
        }
        quizAttemptRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(QuizAttempt quizAttempt) {
        if (quizAttempt.getStudent() == null || quizAttempt.getStudent().getUserId() == null) {
            throw new BadRequestException("Student is required.");
        }
        if (quizAttempt.getQuiz() == null || quizAttempt.getQuiz().getQuizId() == null) {
            throw new BadRequestException("Quiz is required.");
        }
    }

    /** Swaps the id sent by the client for the real Student row. */
    private Student findStudent(Student reference) {
        Long id = reference.getUserId();
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    /** Swaps the id sent by the client for the real Quiz row. */
    private Quiz findQuiz(Quiz reference) {
        Long id = reference.getQuizId();
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }
}
