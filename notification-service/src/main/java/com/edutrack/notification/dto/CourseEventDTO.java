package com.edutrack.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kafka JSON payload for course-created events (must match Course Service producer).
 */
public class CourseEventDTO {

    @JsonProperty("courseId")
    private String courseId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("price")
    private double price;

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
