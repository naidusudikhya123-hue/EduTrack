package com.Edutrack.enrollments.client;

import com.Edutrack.enrollments.dto.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="course-service")
public interface CourseClient {
    @GetMapping("/courses/{courseId}")
    CourseDTO getCourseById(@PathVariable String courseId);
}
