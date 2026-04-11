package com.edutrack.certificateService.dto;

/**
 * Mirrors enrollments UserEnrollmentDTO for Feign (courseId + status).
 */
public class UserEnrollmentFeignDTO {

    private String courseId;
    private String status;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
