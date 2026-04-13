package com.example.assessment_service.service;

import com.example.assessment_service.client.UserClient;
import com.example.assessment_service.dto.*;
import com.example.assessment_service.entity.Question;
import com.example.assessment_service.entity.Quiz;
import com.example.assessment_service.entity.QuizAttempt;
import com.example.assessment_service.repository.QuestionRepository;
import com.example.assessment_service.repository.QuizAttemptRepository;
import com.example.assessment_service.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizServiceImpl implements QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserClient userClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public QuizServiceImpl(QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           QuizAttemptRepository attemptRepository,
                           UserClient userClient, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.userClient = userClient;

        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    // ===================== START QUIZ =====================
    @Override
    public QuizDTO startQuiz(String courseId) {

        Quiz quiz = quizRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questions = questionRepository.findRandomQuestions(quiz.getId());

        Collections.shuffle(questions);

        List<QuestionDTO> questionDTOs = questions.stream()
                .map(this::convertToDTOWithShuffledOptions)
                .toList();

        return new QuizDTO(quiz.getId(), quiz.getTitle(), questionDTOs);
    }

    private QuestionDTO convertToDTOWithShuffledOptions(Question q) {

        List<String> options = new ArrayList<>();
        options.add(q.getOptionA());
        options.add(q.getOptionB());
        options.add(q.getOptionC());
        options.add(q.getOptionD());

        Collections.shuffle(options);

        return new QuestionDTO(q.getId(), q.getQuestionText(), options);
    }

    // ===================== SUBMIT QUIZ =====================
    @Override
    public QuizResultDTO submitQuiz(String quizId, QuizSubmissionDTO submission) {

        List<Question> questions = questionRepository.findByQuizId(quizId);

        int score = 0;

        for (Question q : questions) {
            String studentAnswer = submission.getAnswers().get(q.getId());

            if (q.getCorrectAnswer().equals(studentAnswer)) {
                score++;
            }
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizId(quizId);
        attempt.setUserId(submission.getUserId());
        attempt.setCourseId(submission.getCourseId());
        attempt.setScore(score);
        attempt.setAttemptedAt(LocalDateTime.now());

        attemptRepository.save(attempt);

        return new QuizResultDTO(score);
    }

    // ===================== GET USER ATTEMPTS =====================
    @Override
    public List<UserDTO> getUserById(String userId) {

        List<QuizAttempt> attempts = attemptRepository.findByUserId(userId);

        // 🔥 Circuit Breaker applied here
        UserDTO userResponse = circuitBreakerFactory.create("assessmentUserLookup").run(
                () -> {
                    log.info("Calling USER-SERVICE for userId={}", userId);
                    return userClient.getUserById(userId);
                },
                throwable -> userLookupFallback(userId, throwable)
        );

        return attempts.stream().map(attempt -> {

            Quiz quiz = quizRepository.findById(attempt.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            UserDTO dto = new UserDTO();
            dto.setUserId(userId);
            dto.setUserName(userResponse.getUserName());
            dto.setQuizId(attempt.getQuizId());
            dto.setScore(attempt.getScore());
            dto.setTitle(quiz.getTitle());

            return dto;

        }).toList();
    }

    // ===================== FALLBACK =====================
    private UserDTO userLookupFallback(String userId, Throwable throwable) {

        log.error("🔥 Circuit Breaker triggered for userId={} due to {}",
                userId, throwable.getMessage());

        UserDTO fallbackUser = new UserDTO();
        fallbackUser.setUserId(userId);
        fallbackUser.setUserName("Fallback User");

        return fallbackUser;
    }
}