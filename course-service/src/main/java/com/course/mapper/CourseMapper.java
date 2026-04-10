package com.course.mapper;

import com.course.dto.CourseDTO;
import com.course.entity.CourseEntity;

import java.util.List;

public class CourseMapper {

    public static CourseEntity toEntity(CourseDTO courseDTO) {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setCourseId(courseDTO.getCourseId());
        courseEntity.setDescription(courseDTO.getDescription());
        courseEntity.setPrice(courseDTO.getPrice());
        courseEntity.setStatus(courseDTO.getStatus());
        courseEntity.setTitle(courseDTO.getTitle());
        courseEntity.setInstructorId(courseDTO.getInstructorId());
        return courseEntity;
    }

    public static CourseDTO toDto(CourseEntity courseEntity) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(courseEntity.getCourseId());
        courseDTO.setDescription(courseEntity.getDescription());
        courseDTO.setPrice(courseEntity.getPrice());
        courseDTO.setInstructorId(courseEntity.getInstructorId());
        courseDTO.setStatus(courseEntity.getStatus());
        courseDTO.setTitle(courseEntity.getTitle());
        return courseDTO;
    }

    public static List<CourseDTO> toDtoList(List<CourseEntity> entities) {
        return entities.stream()
                .map(CourseMapper::toDto)
                .toList();
    }
}