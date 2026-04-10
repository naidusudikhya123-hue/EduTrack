package com.example.assessment_service.repository;

import com.example.assessment_service.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,String> {
    List<Question> findByQuizId(String quizId);

    @Query(
            value="select * from question where quiz_id= :quizId order by rand() limit 20",
            nativeQuery = true
    )
    List<Question> findRandomQuestions(@Param("quizId") String quizId);
}