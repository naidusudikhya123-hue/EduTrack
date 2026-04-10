package com.course.dto;

import com.course.enums.CourseStatus;
import jakarta.validation.constraints.*;

public class CourseDTO {

    @Pattern(regexp = "^c\\d+$", message = "CourseId must be in format c1, c2, c3")
    private String courseId;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 150, message = "Title must be max 150 characters")
    private String title;

    @NotBlank(message = "description cannot be empty")
    private String description;

    @PositiveOrZero(message = "price must be zero or positive")
    private double price;

    @Pattern(regexp = "^i\\d+$", message = "instructorId must be in format i1, i2, i3")
    private String instructorId;

    private CourseStatus status;

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }

    public CourseStatus getStatus() { return status; }
    public void setStatus(CourseStatus status) { this.status = status; }
}