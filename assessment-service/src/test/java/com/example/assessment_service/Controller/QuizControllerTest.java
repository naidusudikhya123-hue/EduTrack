package com.example.assessment_service.Controller;

import com.example.assessment_service.dto.QuizDTO;
import com.example.assessment_service.service.QuizServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {

    @Mock
    private QuizServiceImpl quizService;

    @InjectMocks
    private QuizController quizController;

    @Test
    void startQuizDelegatesToService() {
        QuizDTO dto = new QuizDTO("q1", "Java basics", List.of());
        when(quizService.startQuiz("c1")).thenReturn(dto);

        QuizDTO result = quizController.startQuiz("c1");

        assertEquals("q1", result.getQuizId());
        verify(quizService).startQuiz(eq("c1"));
    }
}
