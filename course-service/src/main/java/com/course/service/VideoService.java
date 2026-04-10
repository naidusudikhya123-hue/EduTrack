package com.course.service;

import com.course.dto.VideoDTO;
import com.course.exception.ResourceNotFoundException;
import com.course.exception.VideoAlreadyExistsException;

import java.util.List;

public interface VideoService {

    VideoDTO createVideo(VideoDTO videoDTO)
            throws ResourceNotFoundException, VideoAlreadyExistsException;

    List<VideoDTO> getAllVideos();

    public List<VideoDTO> findByCourse_CourseId(String courseId)
            throws ResourceNotFoundException;

    VideoDTO updateVideo(String videoId, VideoDTO videoDTO)
            throws ResourceNotFoundException;

    long countByCourse_CourseId(String courseId)
            throws ResourceNotFoundException;

    void deleteVideo(String videoId)
            throws ResourceNotFoundException;
}