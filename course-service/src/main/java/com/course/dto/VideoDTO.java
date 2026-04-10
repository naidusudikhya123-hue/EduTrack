package com.course.dto;

import jakarta.validation.constraints.*;

public class VideoDTO {

    @Pattern(regexp = "^v\\d+$", message = "VideoId must be in format v1, v2, v3")
    private String videoId;

    @Pattern(regexp = "^c\\d+$", message = "CourseId must be in format c1, c2, c3")
    @NotNull(message = "Course id is required")
    private String courseId;

    @NotBlank(message = "Video title cannot be empty")
    private String title;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than 0 seconds")
    private Integer duration;

    @NotNull(message = "Video order is required")
    @Positive(message = "Video order must be greater than 0")
    private Integer videoOrder;

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getVideoOrder() { return videoOrder; }
    public void setVideoOrder(Integer videoOrder) { this.videoOrder = videoOrder; }
}