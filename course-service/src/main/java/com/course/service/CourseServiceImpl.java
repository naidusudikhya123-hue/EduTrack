package com.course.service;

import com.course.dto.CourseDTO;
import com.course.entity.CourseEntity;
import com.course.exception.ResourceNotFoundException;
import com.course.mapper.CourseMapper;
import com.course.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        CourseEntity entity = CourseMapper.toEntity(courseDTO);
        CourseEntity savedCourse = courseRepository.save(entity);
        return CourseMapper.toDto(savedCourse);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO getCourseById(String courseId) throws ResourceNotFoundException {
        CourseEntity entity = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + courseId));
        return CourseMapper.toDto(entity);
    }

    @Override
    public CourseDTO updateCourse(CourseDTO courseDTO,String courseId) throws ResourceNotFoundException {
        CourseEntity existing = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + courseId));

        existing.setTitle(courseDTO.getTitle());
        existing.setDescription(courseDTO.getDescription());
        existing.setPrice(courseDTO.getPrice());
        existing.setStatus(courseDTO.getStatus());

        CourseEntity updated = courseRepository.save(existing);
        return CourseMapper.toDto(updated);
    }

    @Override
    public void deleteCourse(String courseId) throws ResourceNotFoundException {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    "Course not found with id: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    @Override
    public List<CourseDTO> findByInstructorId(String instructorId) throws ResourceNotFoundException {
        List<CourseEntity> entities = courseRepository.findByInstructorId(instructorId);
        if(entities.isEmpty()) {
            throw new ResourceNotFoundException("instructor with id not found");
        }
        return CourseMapper.toDtoList(entities);
    }
}