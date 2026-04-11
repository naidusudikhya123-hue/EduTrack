package com.course.controller;

import com.course.dto.CourseDTO;
import com.course.enums.CourseType;
import com.course.exception.ResourceNotFoundException;
import com.course.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @Test
    void getCourseByIdReturnsOk() throws Exception {
        CourseDTO dto = new CourseDTO();
        dto.setCourseId("c1");
        dto.setTitle("Java");
        dto.setDescription("desc");
        dto.setPrice(0);
        dto.setCourseType(CourseType.FREE);
        dto.setInstructorId("i1");

        when(courseService.getCourseById("c1")).thenReturn(dto);

        ResponseEntity<CourseDTO> response = courseController.getCourseById("c1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("c1", response.getBody().getCourseId());
        verify(courseService).getCourseById(eq("c1"));
    }

    @Test
    void getCourseByIdPropagatesNotFound() throws Exception {
        when(courseService.getCourseById("c404")).thenThrow(new ResourceNotFoundException("missing"));

        assertThrows(ResourceNotFoundException.class, () -> courseController.getCourseById("c404"));
    }

    @Test
    void getAllCoursesReturnsOk() {
        when(courseService.getAllCourses()).thenReturn(List.of());

        ResponseEntity<List<CourseDTO>> response = courseController.getAllCourses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}
