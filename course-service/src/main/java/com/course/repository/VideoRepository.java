package com.course.repository;

import com.course.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity,String> {

    List<VideoEntity> findByCourse_CourseId(String courseId);

    long countByCourse_CourseId(String courseId);

    boolean existsByTitleAndCourse_CourseId(String title, String courseId);
}