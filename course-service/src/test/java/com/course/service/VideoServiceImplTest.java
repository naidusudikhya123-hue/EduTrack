package com.course.service;

import com.course.dto.VideoDTO;
import com.course.entity.CourseEntity;
import com.course.entity.VideoEntity;
import com.course.exception.ResourceNotFoundException;
import com.course.exception.VideoAlreadyExistsException;
import com.course.repository.CourseRepository;
import com.course.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private VideoServiceImpl videoService;

    @Test
    void createVideoSavesWhenCourseExistsAndTitleUnique() throws Exception {
        VideoDTO dto = new VideoDTO();
        dto.setCourseId("c1");
        dto.setTitle("Lesson 1");
        dto.setDuration(120);
        dto.setVideoOrder(1);

        CourseEntity course = new CourseEntity();
        course.setCourseId("c1");

        when(courseRepository.findById("c1")).thenReturn(Optional.of(course));
        when(videoRepository.existsByTitleAndCourse_CourseId("Lesson 1", "c1")).thenReturn(false);
        when(videoRepository.save(any(VideoEntity.class))).thenAnswer(invocation -> {
            VideoEntity v = invocation.getArgument(0);
            v.setVideoId("v9");
            return v;
        });

        VideoDTO result = videoService.createVideo(dto);

        assertEquals("Lesson 1", result.getTitle());
        verify(videoRepository).save(any(VideoEntity.class));
    }

    @Test
    void createVideoThrowsWhenCourseMissing() {
        VideoDTO dto = new VideoDTO();
        dto.setCourseId("c404");
        dto.setTitle("x");
        dto.setDuration(1);
        dto.setVideoOrder(1);

        when(courseRepository.findById("c404")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> videoService.createVideo(dto));
    }

    @Test
    void createVideoThrowsWhenDuplicateTitle() {
        VideoDTO dto = new VideoDTO();
        dto.setCourseId("c1");
        dto.setTitle("Dup");
        dto.setDuration(1);
        dto.setVideoOrder(1);

        CourseEntity course = new CourseEntity();
        course.setCourseId("c1");
        when(courseRepository.findById("c1")).thenReturn(Optional.of(course));
        when(videoRepository.existsByTitleAndCourse_CourseId("Dup", "c1")).thenReturn(true);

        assertThrows(VideoAlreadyExistsException.class, () -> videoService.createVideo(dto));
    }

    @Test
    void findByCourseThrowsWhenEmpty() {
        when(videoRepository.findByCourse_CourseId("c1")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> videoService.findByCourse_CourseId("c1"));
    }

    @Test
    void deleteVideoThrowsWhenMissing() {
        when(videoRepository.existsById("v404")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> videoService.deleteVideo("v404"));
    }

    @Test
    void countByCourseThrowsWhenCourseMissing() {
        when(courseRepository.existsById("c404")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> videoService.countByCourse_CourseId("c404"));
    }
}
