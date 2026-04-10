package com.example.assessment_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="quiz_id")
    private String quizId;

    @Column(name = "user_id")
    private String userId;

    @Column(name="course_id")
    private String courseId;

    private int score;
    private LocalDateTime attemptedAt;

    @ElementCollection
    private List<Long> questionIds;

    public QuizAttempt() {}

    public QuizAttempt(Long id, String quizId, String userId, String courseId, int score, LocalDateTime attemptedAt, List<Long> questionIds) {
        this.id = id;
        this.quizId = quizId;
        this.userId = userId;
        this.courseId = courseId;
        this.score = score;
        this.attemptedAt = attemptedAt;
        this.questionIds = questionIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }
}