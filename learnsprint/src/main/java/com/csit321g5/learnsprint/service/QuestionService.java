package com.csit321g5.learnsprint.service;

import com.csit321g5.learnsprint.entity.Question;
import com.csit321g5.learnsprint.entity.Quiz;
import com.csit321g5.learnsprint.exception.BadRequestException;
import com.csit321g5.learnsprint.exception.ResourceNotFoundException;
import com.csit321g5.learnsprint.repository.QuestionRepository;
import com.csit321g5.learnsprint.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionService(QuestionRepository questionRepository,
                           QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    /** CREATE */
    public Question create(Question question) {
        validate(question);
        question.setQuestionId(null);
        question.setQuiz(findQuiz(question.getQuiz()));
        return questionRepository.save(question);
    }

    /** READ all */
    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    /** READ one */
    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
    }

    /** UPDATE */
    public Question update(Long id, Question changes) {
        Question existing = findById(id);
        validate(changes);
        existing.setQuestionText(changes.getQuestionText());
        existing.setChoices(changes.getChoices());
        existing.setCorrectAnswer(changes.getCorrectAnswer());
        existing.setQuiz(findQuiz(changes.getQuiz()));
        return questionRepository.save(existing);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question", id);
        }
        questionRepository.deleteById(id);
    }

    /** Checks the required values before anything is saved. */
    private void validate(Question question) {
        if (isBlank(question.getQuestionText())) {
            throw new BadRequestException("Question text is required.");
        }
        if (question.getQuiz() == null || question.getQuiz().getQuizId() == null) {
            throw new BadRequestException("Quiz is required.");
        }
    }

    /** Swaps the id sent by the client for the real Quiz row. */
    private Quiz findQuiz(Quiz reference) {
        Long id = reference.getQuizId();
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
