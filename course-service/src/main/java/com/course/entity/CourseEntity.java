package com.course.entity;

import com.course.enums.CourseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="course")
public class CourseEntity {

    @Id
    private String courseId;

    private String title;
    private String description;
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType courseType;

    @Column(nullable = false)
    private String instructorId;


    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VideoEntity> videos;

    public CourseEntity() {}

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

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }


    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<VideoEntity> getVideos() { return videos; }
    public void setVideos(List<VideoEntity> videos) { this.videos = videos; }
}