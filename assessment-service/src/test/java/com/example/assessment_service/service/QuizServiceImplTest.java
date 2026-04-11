package com.example.assessment_service.service;

import com.example.assessment_service.client.CourseClient;
import com.example.assessment_service.dto.CourseDTO;
import com.example.assessment_service.dto.QuizDTO;
import com.example.assessment_service.dto.QuizResultDTO;
import com.example.assessment_service.dto.QuizSubmissionDTO;
import com.example.assessment_service.entity.Question;
import com.example.assessment_service.entity.Quiz;
import com.example.assessment_service.entity.QuizAttempt;
import com.example.assessment_service.repository.QuestionRepository;
import com.example.assessment_service.repository.QuizAttemptRepository;
import com.example.assessment_service.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizAttemptRepository attemptRepository;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void startQuizReturnsQuizWithQuestions() {

        // Arrange
        Quiz quiz = new Quiz();
        quiz.setId("q1");
        quiz.setCourseId("c1");
        quiz.setTitle("Java Quiz");

        Question question1 = new Question(
                "1", "q1", "What is JVM?",
                "A", "B", "C", "D", "A"
        );

        Question question2 = new Question(
                "2", "q1", "What is JDK?",
                "A1", "B1", "C1", "D1", "B1"
        );

        when(quizRepository.findByCourseId("c1"))
                .thenReturn(Optional.of(quiz));

        CourseDTO courseDto = new CourseDTO();
        courseDto.setCourseId("c1");
        when(courseClient.getCourseById("c1")).thenReturn(courseDto);

        // ✅ FIXED HERE
        when(questionRepository.findRandomQuestions("q1"))
                .thenReturn(new ArrayList<>(List.of(question1, question2)));

        // Act
        QuizDTO result = quizService.startQuiz("c1");

        // Assert
        assertNotNull(result);
        assertEquals("q1", result.getQuizId());
        assertEquals("Java Quiz", result.getTitle());
        assertEquals(2, result.getQuestions().size());

        verify(quizRepository).findByCourseId("c1");
        verify(questionRepository).findRandomQuestions("q1");
    }

    @Test
    void startQuizThrowsWhenQuizMissing() {
        CourseDTO courseDto = new CourseDTO();
        courseDto.setCourseId("c404");
        when(courseClient.getCourseById("c404")).thenReturn(courseDto);
        when(quizRepository.findByCourseId("c404")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.startQuiz("c404"));
    }

    @Test
    void submitQuizCalculatesScoreAndSavesAttempt() {
        Question question1 = new Question("1", "q1", "Q1", "A", "B", "C", "D", "A");
        Question question2 = new Question("2", "q1", "Q2", "A", "B", "C", "D", "C");

        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setUserId("u1");
        submission.setCourseID("c1");
        submission.setAnswers(Map.of("1", "A", "2", "B"));

        CourseDTO courseDto = new CourseDTO();
        courseDto.setCourseId("c1");
        when(courseClient.getCourseById("c1")).thenReturn(courseDto);
        when(questionRepository.findByQuizId("q1")).thenReturn(List.of(question1, question2));

        QuizResultDTO result = quizService.submitQuiz("q1", submission);

        assertEquals(1, result.getScore());

        ArgumentCaptor<QuizAttempt> captor = ArgumentCaptor.forClass(QuizAttempt.class);
        verify(attemptRepository).save(captor.capture());
        assertEquals("u1", captor.getValue().getUserId());
        assertEquals("c1", captor.getValue().getCourseId());
        assertEquals(1, captor.getValue().getScore());
    }
}
