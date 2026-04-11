package com.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kafka payload for course-created events (JSON).
 */
public class CourseEventDTO {

    @JsonProperty("courseId")
    private String courseId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("price")
    private double price;

    public CourseEventDTO() {
    }

    public CourseEventDTO(String courseId, String title, double price) {
        this.courseId = courseId;
        this.title = title;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
