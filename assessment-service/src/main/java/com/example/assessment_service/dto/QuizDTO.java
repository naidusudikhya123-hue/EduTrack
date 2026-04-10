package com.example.assessment_service.dto;

import java.util.List;

public class QuizDTO {
    private String quizId;
    private String title;
    private List<QuestionDTO> questions;

    public QuizDTO()
    {

    }
    public QuizDTO(String quizId, String title, List<QuestionDTO> questions) {
        this.quizId = quizId;
        this.title = title;
        this.questions = questions;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}