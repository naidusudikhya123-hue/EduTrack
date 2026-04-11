package com.example.assessment_service.dto;

/**
 * Minimal course payload from course-service for existence checks.
 */
public class CourseDTO {

    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
