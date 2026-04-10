package com.example.assessment_service.Controller;

import com.example.assessment_service.dto.QuizDTO;
import com.example.assessment_service.dto.QuizResultDTO;
import com.example.assessment_service.dto.QuizSubmissionDTO;
import com.example.assessment_service.dto.UserDTO;
import com.example.assessment_service.service.QuizServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizServiceImpl quizService;

    public QuizController(QuizServiceImpl quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/start/{courseId}")
    public QuizDTO startQuiz(@PathVariable String courseId)
    {
        return quizService.startQuiz(courseId);
    }

    @PostMapping("/submit/{quizId}")
    public QuizResultDTO submitQuiz(@PathVariable String quizId, @RequestBody QuizSubmissionDTO submission)
    {
        return quizService.submitQuiz(quizId,submission);
    }

    @GetMapping("users/{userId}")
    public List<UserDTO>  getUserById(@PathVariable String userId)
    {
        List<UserDTO> userDetails=quizService.getUserById(userId);
        return userDetails;
    }



}