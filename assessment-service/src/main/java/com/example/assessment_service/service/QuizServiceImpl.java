package com.example.assessment_service.service;

import com.example.assessment_service.client.UserClient;
import com.example.assessment_service.dto.*;
import com.example.assessment_service.entity.Question;
import com.example.assessment_service.entity.Quiz;
import com.example.assessment_service.entity.QuizAttempt;
import com.example.assessment_service.repository.QuestionRepository;
import com.example.assessment_service.repository.QuizAttemptRepository;
import com.example.assessment_service.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserClient userClient;

    public QuizServiceImpl(QuizRepository quizRepository, QuestionRepository questionRepository, QuizAttemptRepository attemptRepository,UserClient userClient) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.userClient=userClient;
    }

    @Override
    public QuizDTO startQuiz(String courseId) {

        Quiz quiz = quizRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

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

        UserDTO userResponse = userClient.getUserById(userId);

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

}