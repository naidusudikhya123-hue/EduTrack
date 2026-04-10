package com.example.assessment_service.dto;

import java.util.Map;

public class QuizSubmissionDTO {

    private String userId;
    private String courseID;
    private Map<String,String> answers;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    public String getCourseId() {
        return courseID;
    }
}