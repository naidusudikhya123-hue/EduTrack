package com.example.Payment_Service.dto;

import jakarta.validation.constraints.NotBlank;

public class EnrollmentRequestDTO {
    @NotBlank
    private String userId;

    @NotBlank
    private String courseId;

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
}
