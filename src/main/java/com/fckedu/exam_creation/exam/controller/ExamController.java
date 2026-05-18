package com.fckedu.exam_creation.exam.controller;

import com.fckedu.exam_creation.exam.dto.request.GenerateExamPayload;
import com.fckedu.exam_creation.exam.dto.response.ExamDetailDTO;
import com.fckedu.exam_creation.exam.usecase.ExamUsecase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam")
public class ExamController {
    private final ExamUsecase examUsecase;

    public ExamController(ExamUsecase examUsecase) {
        this.examUsecase = examUsecase;
    }

    @PostMapping("/generate")
    public String generateExam(@RequestBody GenerateExamPayload payload) {
        return examUsecase.generateExam(payload.getDraftId());
    }

    @GetMapping("/{examId}")
    public ExamDetailDTO getExamById(@PathVariable String examId) {
        return examUsecase.getExamById(examId);
    }
}
