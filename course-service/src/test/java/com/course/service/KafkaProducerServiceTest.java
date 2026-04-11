package com.course.service;

import com.course.entity.CourseEntity;
import com.course.enums.CourseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendCourseCreatedEventSendsJsonPayload() throws Exception {
        KafkaProducerService kafkaProducerService = new KafkaProducerService(kafkaTemplate, objectMapper);

        CourseEntity course = new CourseEntity();
        course.setCourseId("c99");
        course.setTitle("Kafka Basics");
        course.setPrice(199.5);
        course.setCourseType(CourseType.PAID);

        kafkaProducerService.sendCourseCreatedEvent(course);

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(
                eq(KafkaProducerService.COURSE_CREATED_TOPIC),
                eq("c99"),
                jsonCaptor.capture()
        );

        String json = jsonCaptor.getValue();
        assertTrue(json.contains("\"courseId\":\"c99\""));
        assertTrue(json.contains("\"title\":\"Kafka Basics\""));
        assertTrue(json.contains("\"price\":199.5"));
    }

    @Test
    void sendCourseCreatedEventDoesNothingWhenCourseNull() {
        KafkaProducerService kafkaProducerService = new KafkaProducerService(kafkaTemplate, objectMapper);
        kafkaProducerService.sendCourseCreatedEvent(null);
        org.mockito.Mockito.verifyNoInteractions(kafkaTemplate);
    }
}
