package com.example.assessment_service.dto;

public class QuizResultDTO {

    private int score;

    public QuizResultDTO(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}