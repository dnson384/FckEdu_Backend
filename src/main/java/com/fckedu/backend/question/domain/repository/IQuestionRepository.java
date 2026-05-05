package com.fckedu.backend.question.domain.repository;

import com.fckedu.backend.question.domain.entity.QuestionEntity;

import java.util.List;

public interface IQuestionRepository {
    boolean saveQuestions(List<QuestionEntity> questions);
}
