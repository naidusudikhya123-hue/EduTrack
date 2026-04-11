package com.example.Payment_Service.dto;

/**
 * JSON body for enrollments POST /enrollments (matches EnrollmentCreateRequestDTO).
 */
public class EnrollmentCreateFeignDTO {

    private String userId;
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
