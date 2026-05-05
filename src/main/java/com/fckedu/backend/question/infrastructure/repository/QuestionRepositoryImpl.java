package com.fckedu.backend.question.infrastructure.repository;

import com.fckedu.backend.question.domain.entity.QuestionEntity;
import com.fckedu.backend.question.domain.repository.IQuestionRepository;
import com.fckedu.backend.question.infrastructure.document.QuestionDocument;
import com.fckedu.backend.question.infrastructure.mapper.QuestionMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
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
                .map(question -> {
                    QuestionDocument newDoc = questionMapper.toDocument(question);
                    newDoc.setCreatedAt(LocalDateTime.now());
                    newDoc.setUpdatedAt(LocalDateTime.now());
                    return newDoc;
                })
                .toList();
        
        mongoTemplate.insertAll(questionsDoc);
        return true;
    }
}
