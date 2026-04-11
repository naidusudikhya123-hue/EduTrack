package com.edutrack.notification.kafka;

import com.edutrack.notification.dto.CourseEventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void consumeCourseCreatedParsesJson() throws Exception {
        String json = "{\"courseId\":\"c1\",\"title\":\"Kafka 101\",\"price\":49.99}";
        CourseEventDTO dto = new CourseEventDTO();
        dto.setCourseId("c1");
        dto.setTitle("Kafka 101");
        dto.setPrice(49.99);

        when(objectMapper.readValue(eq(json), eq(CourseEventDTO.class))).thenReturn(dto);

        kafkaConsumerService.consumeCourseCreated(json);

        verify(objectMapper).readValue(eq(json), eq(CourseEventDTO.class));
    }
}
