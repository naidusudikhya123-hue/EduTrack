package com.example.assessment_service.client;

import com.example.assessment_service.dto.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service")
public interface CourseClient {

    @GetMapping("/courses/{courseId}")
    CourseDTO getCourseById(@PathVariable("courseId") String courseId);
}
