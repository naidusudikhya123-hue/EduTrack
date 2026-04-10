package com.example.assessment_service.service;

import com.example.assessment_service.dto.QuizDTO;
import com.example.assessment_service.dto.QuizResultDTO;
import com.example.assessment_service.dto.QuizSubmissionDTO;
import com.example.assessment_service.dto.UserDTO;

import java.util.List;

public interface QuizService {

    QuizDTO startQuiz(String courseId);
    QuizResultDTO submitQuiz(String quizId, QuizSubmissionDTO submission);

    List<UserDTO> getUserById(String userId);
}