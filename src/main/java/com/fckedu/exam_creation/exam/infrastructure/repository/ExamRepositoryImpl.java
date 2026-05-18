package com.fckedu.exam_creation.exam.infrastructure.repository;

import com.fckedu.exam_creation.common.exception.NotFoundException;
import com.fckedu.exam_creation.exam.domain.entity.ExamEntity;
import com.fckedu.exam_creation.exam.domain.repository.IExamRepository;
import com.fckedu.exam_creation.exam.infrastructure.document.ExamDocument;
import com.fckedu.exam_creation.exam.infrastructure.mapper.ExamMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExamRepositoryImpl implements IExamRepository {
    private final MongoTemplate mongoTemplate;
    private final ExamMapper mapper;

    public ExamRepositoryImpl(MongoTemplate mongoTemplate, ExamMapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
    }

    @Override
    public String saveExam(ExamEntity entity) {
        ExamDocument doc = mapper.entityToDocument(entity);
        ExamDocument result = mongoTemplate.insert(doc);
        return result.getId();
    }

    @Override
    public ExamEntity getExamById(String examId) {
        ExamDocument exam = mongoTemplate.findById(examId, ExamDocument.class);
        if (exam == null) {
            throw new NotFoundException("Không tồn tại bài kiểm tra");
        }
        return mapper.docToEntity(exam);
    }
}
