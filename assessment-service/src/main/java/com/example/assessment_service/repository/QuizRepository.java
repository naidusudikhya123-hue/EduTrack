package com.example.assessment_service.repository;

import com.example.assessment_service.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz,String> {
    Optional<Quiz> findByCourseId(String courseId);
}