package com.example.assessment_service.repository;

import com.example.assessment_service.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt,String> {
    List<QuizAttempt> findByUserIdAndQuizId(String userId,String quizId);

    List<QuizAttempt> findByUserId(String userId);
}