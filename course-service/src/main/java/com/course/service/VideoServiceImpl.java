package com.course.service;

import com.course.dto.VideoDTO;
import com.course.entity.CourseEntity;
import com.course.entity.VideoEntity;
import com.course.exception.ResourceNotFoundException;
import com.course.exception.VideoAlreadyExistsException;
import com.course.mapper.VideoMapper;
import com.course.repository.CourseRepository;
import com.course.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;

    public VideoServiceImpl(VideoRepository videoRepository,
                            CourseRepository courseRepository) {
        this.videoRepository = videoRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public VideoDTO createVideo(VideoDTO videoDTO)
            throws ResourceNotFoundException, VideoAlreadyExistsException {

        CourseEntity course = courseRepository.findById(videoDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + videoDTO.getCourseId()));

        boolean exists = videoRepository
                .existsByTitleAndCourse_CourseId(
                        videoDTO.getTitle(),
                        videoDTO.getCourseId());

        if (exists) {
            throw new VideoAlreadyExistsException(
                    "Video with title '" + videoDTO.getTitle() +
                            "' already exists in course " + videoDTO.getCourseId());
        }

        VideoEntity video = VideoMapper.toEntity(videoDTO);
        video.setCourse(course);

        VideoEntity savedVideo = videoRepository.save(video);

        return VideoMapper.toDto(savedVideo);
    }

    @Override
    public List<VideoDTO> getAllVideos() {
        return videoRepository.findAll()
                .stream()
                .map(VideoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VideoDTO> findByCourse_CourseId(String courseId)
            throws ResourceNotFoundException {

        List<VideoEntity> videos =
                videoRepository.findByCourse_CourseId(courseId);

        if (videos.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No videos found for course id: " + courseId);
        }

        return videos.stream()
                .map(VideoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VideoDTO updateVideo(String videoId, VideoDTO videoDTO)
            throws ResourceNotFoundException {

        VideoEntity existingVideo = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Video not found with id: " + videoId));

        existingVideo.setTitle(videoDTO.getTitle());
        existingVideo.setDuration(videoDTO.getDuration());
        existingVideo.setVideoOrder(videoDTO.getVideoOrder());

        VideoEntity updatedVideo = videoRepository.save(existingVideo);

        return VideoMapper.toDto(updatedVideo);
    }

    @Override
    public void deleteVideo(String videoId)
            throws ResourceNotFoundException {

        if (!videoRepository.existsById(videoId)) {
            throw new ResourceNotFoundException(
                    "Video not found with id: " + videoId);
        }

        videoRepository.deleteById(videoId);
    }

    @Override
    public long countByCourse_CourseId(String courseId)
            throws ResourceNotFoundException {

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    "Course not found with id: " + courseId);
        }

        return videoRepository.countByCourse_CourseId(courseId);
    }
}