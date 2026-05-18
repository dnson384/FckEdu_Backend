package com.fckedu.exam_creation.exam.domain.repository;

import com.fckedu.exam_creation.exam.domain.entity.ExamEntity;

public interface IExamRepository {
    String saveExam(ExamEntity payload);

    ExamEntity getExamById(String examId);
}
