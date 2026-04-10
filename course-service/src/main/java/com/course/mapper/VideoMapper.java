package com.course.mapper;

import com.course.dto.VideoDTO;
import com.course.entity.VideoEntity;

public class VideoMapper {

    public static VideoEntity toEntity(VideoDTO videoDTO) {
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setVideoId(videoDTO.getVideoId());
        videoEntity.setTitle(videoDTO.getTitle());
        videoEntity.setDuration(videoDTO.getDuration());
        videoEntity.setVideoOrder(videoDTO.getVideoOrder());
        return videoEntity;
    }

    public static VideoDTO toDto(VideoEntity videoEntity) {
        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setTitle(videoEntity.getTitle());
        videoDTO.setVideoOrder(videoEntity.getVideoOrder());
        videoDTO.setDuration(videoEntity.getDuration());
        videoDTO.setVideoId(videoEntity.getVideoId());
        videoDTO.setCourseId(videoEntity.getCourse().getCourseId());
        return videoDTO;
    }
}