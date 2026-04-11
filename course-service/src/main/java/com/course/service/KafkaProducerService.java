package com.course.service;

import com.course.dto.CourseEventDTO;
import com.course.entity.CourseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    public static final String COURSE_CREATED_TOPIC = "course-created-topic";

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Publishes a JSON event after a new course is persisted.
     */
    public void sendCourseCreatedEvent(CourseEntity course) {
        if (course == null) {
            return;
        }
        CourseEventDTO event = new CourseEventDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getPrice()
        );
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(COURSE_CREATED_TOPIC, course.getCourseId(), json);
            log.debug("Published course-created event for courseId={}", course.getCourseId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize course event for courseId={}", course.getCourseId(), e);
        }
    }
}
