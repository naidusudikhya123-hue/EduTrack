package com.Edutrack.enrollments.dto;

import jakarta.validation.constraints.NotBlank;

public class EnrollmentCreateRequestDTO {
    @NotBlank(message = "UserId is mandatory")
    private String userId;

    @NotBlank(message = "CourseId is mandatory")
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

    public EnrollmentCreateRequestDTO() {}

    public EnrollmentCreateRequestDTO(String userId, String courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }
}