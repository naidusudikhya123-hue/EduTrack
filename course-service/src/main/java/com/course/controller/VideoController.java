package com.course.controller;

import com.course.dto.VideoDTO;
import com.course.exception.ResourceNotFoundException;
import com.course.exception.VideoAlreadyExistsException;
import com.course.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(
            @Valid @RequestBody VideoDTO videoDTO)
            throws ResourceNotFoundException, VideoAlreadyExistsException {

        VideoDTO createdVideo = videoService.createVideo(videoDTO);
        return new ResponseEntity<>(createdVideo, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/getd/{courseId}")
    public ResponseEntity<?> getVideoByCourseId(@PathVariable String courseId)
            throws ResourceNotFoundException {

        return ResponseEntity.ok(
                videoService.findByCourse_CourseId(courseId));
    }

    @PutMapping("/{videoId}")
    public ResponseEntity<VideoDTO> updateVideo(
            @PathVariable String videoId,
            @Valid @RequestBody VideoDTO videoDTO)
            throws ResourceNotFoundException {

        return ResponseEntity.ok(
                videoService.updateVideo(videoId, videoDTO));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<String> deleteVideo(
            @PathVariable String videoId)
            throws ResourceNotFoundException {

        videoService.deleteVideo(videoId);
        return ResponseEntity.ok("Video deleted successfully");
    }

    @GetMapping("/count/{courseId}")
    public ResponseEntity<?> countByCourse_CourseId(@PathVariable String courseId)
            throws ResourceNotFoundException {

        return new ResponseEntity<>(
                videoService.countByCourse_CourseId(courseId),
                HttpStatus.OK);
    }
}