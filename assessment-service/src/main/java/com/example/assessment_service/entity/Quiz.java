package com.example.assessment_service.entity;

import jakarta.persistence.*;

@Entity
public class Quiz {
    @Id
    private String id;

    @Column(name="course_id")
    private String courseId;

    private String title;
    private int totalQuestions;

    public Quiz()
    {

    }
    public Quiz(String id, String courseId, String title, int totalQuestions) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.totalQuestions = totalQuestions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}