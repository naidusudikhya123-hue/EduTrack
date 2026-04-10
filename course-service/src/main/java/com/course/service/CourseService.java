package com.course.service;

import com.course.dto.CourseDTO;
import com.course.exception.ResourceNotFoundException;

import java.util.List;

public interface CourseService {

    public CourseDTO createCourse(CourseDTO courseDTO);
    public List<CourseDTO> getAllCourses();
    public CourseDTO getCourseById(String courseId) throws ResourceNotFoundException;
    public CourseDTO updateCourse(CourseDTO courseDTO,String courseId) throws ResourceNotFoundException;
    public void deleteCourse(String courseId) throws ResourceNotFoundException;
    List<CourseDTO> findByInstructorId(String InstructorId) throws ResourceNotFoundException;
}