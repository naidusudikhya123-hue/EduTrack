package com.course.controller;

import com.course.dto.CourseDTO;
import com.course.exception.ResourceNotFoundException;
import com.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable String courseId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable String courseId,
            @Valid @RequestBody CourseDTO courseDTO)
            throws ResourceNotFoundException {

        return ResponseEntity.ok(courseService.updateCourse(courseDTO, courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable String courseId)
            throws ResourceNotFoundException {

        courseService.deleteCourse(courseId);
        return ResponseEntity.ok("Course deleted successfully");
    }

    @GetMapping("/instructors/{instructorId}")
    public ResponseEntity<?> getByInstructorId(@PathVariable String instructorId)
            throws ResourceNotFoundException {

        return new ResponseEntity<>(
                courseService.findByInstructorId(instructorId),
                HttpStatus.OK);
    }
}