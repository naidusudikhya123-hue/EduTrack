package com.edutrack.notification.kafka;

import com.edutrack.notification.dto.CourseEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;

    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "course-created-topic", groupId = "notification-group")
    public void consumeCourseCreated(String message) {
        try {
            CourseEventDTO event = objectMapper.readValue(message, CourseEventDTO.class);
            log.info("New Course Added: {} with price {}", event.getTitle(), event.getPrice());
            System.out.printf("New Course Added: %s with price %s%n", event.getTitle(), event.getPrice());
        } catch (Exception e) {
            log.error("Failed to process course-created message: {}", message, e);
        }
    }
}
