package com.fckedu.backend.question.infrastructure.repository;

import com.fckedu.backend.question.domain.entity.QuestionEntity;
import com.fckedu.backend.question.domain.repository.IQuestionRepository;
import com.fckedu.backend.question.infrastructure.document.QuestionDocument;
import com.fckedu.backend.question.infrastructure.mapper.QuestionMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class QuestionRepositoryImpl implements IQuestionRepository {
    private final MongoTemplate mongoTemplate;
    private final QuestionMapper questionMapper;

    public QuestionRepositoryImpl(MongoTemplate mongoTemplate, QuestionMapper questionMapper) {
        this.mongoTemplate = mongoTemplate;
        this.questionMapper = questionMapper;
    }

    @Override
    public boolean saveQuestions(List<QuestionEntity> questions) {
        List<QuestionDocument> questionsDoc = questions.stream()
                .map(questionMapper::toDocument).toList();
        mongoTemplate.save(questionsDoc);
        return true;
    }
}
