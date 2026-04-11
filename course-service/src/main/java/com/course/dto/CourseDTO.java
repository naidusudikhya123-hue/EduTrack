package com.course.dto;

import com.course.enums.CourseType;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotNull(message = "courseType is required (FREE or PAID)")
    private CourseType courseType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isFree;

    @Pattern(regexp = "^i\\d+$", message = "instructorId must be in format i1, i2, i3")
    private String instructorId;

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public CourseType getCourseType() { return courseType; }
    public void setCourseType(CourseType courseType) { this.courseType = courseType; }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
}