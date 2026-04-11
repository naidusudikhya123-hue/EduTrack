package com.course.controller;

import com.course.dto.VideoDTO;
import com.course.service.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    @Test
    void getAllVideosReturnsOk() {
        when(videoService.getAllVideos()).thenReturn(List.of());

        ResponseEntity<List<VideoDTO>> response = videoController.getAllVideos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createVideoReturnsCreated() throws Exception {
        VideoDTO body = new VideoDTO();
        body.setCourseId("c1");
        body.setTitle("Intro");
        body.setDuration(60);
        body.setVideoOrder(1);

        VideoDTO created = new VideoDTO();
        created.setVideoId("v1");
        created.setCourseId("c1");
        created.setTitle("Intro");
        created.setDuration(60);
        created.setVideoOrder(1);

        when(videoService.createVideo(any(VideoDTO.class))).thenReturn(created);

        ResponseEntity<VideoDTO> response = videoController.createVideo(body);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("v1", response.getBody().getVideoId());
        verify(videoService).createVideo(any(VideoDTO.class));
    }
}
