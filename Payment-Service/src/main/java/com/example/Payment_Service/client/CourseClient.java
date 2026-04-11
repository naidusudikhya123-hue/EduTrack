package com.example.Payment_Service.client;

import com.example.Payment_Service.dto.CourseDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service")
public interface CourseClient {
    @GetMapping("/courses/{courseId}")
    CourseDetailsDTO getCourseById(@PathVariable("courseId") String courseId);
}
