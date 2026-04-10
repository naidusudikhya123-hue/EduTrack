package com.course.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "video")
public class VideoEntity {

    @Id
    @Column(name = "video_id")
    private String videoId;

    private String title;
    private Integer duration;
    private Integer videoOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "courseId", nullable = false)
    private CourseEntity course;

    public VideoEntity() {}

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getVideoOrder() { return videoOrder; }
    public void setVideoOrder(Integer videoOrder) { this.videoOrder = videoOrder; }

    public CourseEntity getCourse() { return course; }
    public void setCourse(CourseEntity course) { this.course = course; }
}