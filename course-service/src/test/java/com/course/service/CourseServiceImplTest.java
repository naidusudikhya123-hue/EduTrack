package com.course.service;

import com.course.dto.CourseDTO;
import com.course.entity.CourseEntity;
import com.course.enums.CourseStatus;
import com.course.exception.ResourceNotFoundException;
import com.course.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void createCourseReturnsMappedSavedCourse() {
        CourseEntity savedEntity = new CourseEntity();
        savedEntity.setCourseId("c1");
        savedEntity.setTitle("Spring Boot");
        savedEntity.setDescription("Backend");
        savedEntity.setPrice(499);
        savedEntity.setInstructorId("i1");
        savedEntity.setStatus(CourseStatus.APPROVED);

        CourseDTO request = new CourseDTO();
        request.setTitle("Spring Boot");
        request.setDescription("Backend");
        request.setPrice(499);
        request.setInstructorId("i1");
        request.setStatus(CourseStatus.APPROVED);

        when(courseRepository.save(any(CourseEntity.class))).thenReturn(savedEntity);

        CourseDTO result = courseService.createCourse(request);

        assertEquals("c1", result.getCourseId());
        assertEquals("Spring Boot", result.getTitle());
    }

    @Test
    void updateCourseThrowsWhenCourseMissing() {
        when(courseRepository.findById("c404")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(new CourseDTO(), "c404"));
    }

    @Test
    void findByInstructorIdThrowsWhenNoCoursesExist() {
        when(courseRepository.findByInstructorId("i404")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> courseService.findByInstructorId("i404"));
    }

    @Test
    void deleteCourseDeletesExistingCourse() throws Exception {
        when(courseRepository.existsById("c1")).thenReturn(true);

        courseService.deleteCourse("c1");

        verify(courseRepository).deleteById("c1");
    }
}
