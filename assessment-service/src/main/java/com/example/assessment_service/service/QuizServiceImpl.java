package com.example.assessment_service.service;

import com.example.assessment_service.client.CourseClient;
import com.example.assessment_service.client.UserClient;
import com.example.assessment_service.dto.CourseDTO;
import com.example.assessment_service.dto.*;
import com.example.assessment_service.exception.ResourceNotFoundException;
import com.example.assessment_service.exception.UpstreamServiceException;
import com.example.assessment_service.entity.Question;
import com.example.assessment_service.entity.Quiz;
import com.example.assessment_service.entity.QuizAttempt;
import com.example.assessment_service.repository.QuestionRepository;
import com.example.assessment_service.repository.QuizAttemptRepository;
import com.example.assessment_service.repository.QuizRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserClient userClient;
    private final CourseClient courseClient;

    public QuizServiceImpl(QuizRepository quizRepository, QuestionRepository questionRepository,
                           QuizAttemptRepository attemptRepository, UserClient userClient,
                           CourseClient courseClient) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.userClient = userClient;
        this.courseClient = courseClient;
    }

    @Override
    public QuizDTO startQuiz(String courseId) {

        ensureCourseExists(courseId);

        Quiz quiz = quizRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found for course: " + courseId));

        List<Question> questions =
                questionRepository.findRandomQuestions(quiz.getId());

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

    @Override
    public QuizResultDTO submitQuiz(String quizId,
                                    QuizSubmissionDTO submission) {

        ensureCourseExists(submission.getCourseId());

        List<Question> questions =
                questionRepository.findByQuizId(quizId);

        int score = 0;

        for (Question q : questions) {

            String studentAnswer =
                    submission.getAnswers().get(q.getId());

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


    @Override
    public List<UserDTO> getUserById(String userId) {

        List<QuizAttempt> attempts = attemptRepository.findByUserId(userId);

        UserDTO userResponse;
        try {
            userResponse = userClient.getUserById(userId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("User not found: " + userId);
            }
            throw new UpstreamServiceException("User service error: " + e.getMessage(), e);
        }

        return attempts.stream().map(attempt -> {

            Quiz quiz = quizRepository.findById(attempt.getQuizId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + attempt.getQuizId()));

            UserDTO dto = new UserDTO();
            dto.setUserId(userId);
            dto.setUserName(userResponse.getUserName());
            dto.setQuizId(attempt.getQuizId());
            dto.setScore(attempt.getScore());
            dto.setTitle(quiz.getTitle());

            return dto;

        }).toList();
    }

    private void ensureCourseExists(String courseId) {
        try {
            CourseDTO course = courseClient.getCourseById(courseId);
            if (course == null) {
                throw new ResourceNotFoundException("Course not found: " + courseId);
            }
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Course not found: " + courseId);
            }
            throw new UpstreamServiceException("Course service error: " + e.getMessage(), e);
        }
    }

}