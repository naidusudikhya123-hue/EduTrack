package com.edutrack.certificateService.dto;

public class CertificateRequestDTO {
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