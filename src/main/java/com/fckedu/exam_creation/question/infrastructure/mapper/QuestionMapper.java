package com.fckedu.exam_creation.question.infrastructure.mapper;

import com.fckedu.exam_creation.question.domain.entity.OptionDataEntity;
import com.fckedu.exam_creation.question.domain.entity.QuestionContentEntity;
import com.fckedu.exam_creation.question.domain.entity.QuestionEntity;
import com.fckedu.exam_creation.question.domain.entity.VariablesEntity;
import com.fckedu.exam_creation.question.infrastructure.document.OptionDataDoc;
import com.fckedu.exam_creation.question.infrastructure.document.QuestionContentDoc;
import com.fckedu.exam_creation.question.infrastructure.document.QuestionDocument;
import com.fckedu.exam_creation.question.infrastructure.document.VariablesDoc;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionMapper {
    public QuestionEntity toEntity(QuestionDocument raw) {
        QuestionEntity newQuestionEntity = new QuestionEntity();
        newQuestionEntity.setId(raw.getId());
        newQuestionEntity.setChapterId(raw.getChapterId());
        newQuestionEntity.setLessonId(raw.getLessonId());
        newQuestionEntity.setDifficultyLevel(raw.getDifficultyLevel());
        newQuestionEntity.setLearningOutcomes(raw.getLearningOutcomes());
        newQuestionEntity.setQuestionType(raw.getQuestionType());

        // question
        QuestionContentDoc questionContentDoc = raw.getQuestion();
        VariablesEntity questionVariablesEntity = new VariablesEntity(
                questionContentDoc.getVariables().getMath(),
                questionContentDoc.getVariables().getImage()
        );

        QuestionContentEntity questionContentEntity = new QuestionContentEntity(
                questionContentDoc.getTemplate(),
                questionVariablesEntity
        );

        newQuestionEntity.setQuestion(questionContentEntity);

        // options
        List<OptionDataEntity> optionDataEntities = raw.getOptions().stream()
                .map(this::mapOptionToEntity).toList();

        newQuestionEntity.setOptions(optionDataEntities);

        return newQuestionEntity;
    }

    public QuestionDocument toDocument(QuestionEntity entity) {
        QuestionDocument newQuestionDoc = new QuestionDocument();
        newQuestionDoc.setId(entity.getId());
        newQuestionDoc.setChapterId(entity.getChapterId());
        newQuestionDoc.setLessonId(entity.getLessonId());
        newQuestionDoc.setDifficultyLevel(entity.getDifficultyLevel());
        newQuestionDoc.setLearningOutcomes(entity.getLearningOutcomes());
        newQuestionDoc.setQuestionType(entity.getQuestionType());

        // question
        QuestionContentEntity questionContentEntity = entity.getQuestion();
        VariablesDoc questionVariablesDoc = new VariablesDoc(
                questionContentEntity.getVariables().getMath(),
                questionContentEntity.getVariables().getImage()
        );

        QuestionContentDoc questionContentDoc = new QuestionContentDoc(
                questionContentEntity.getTemplate(),
                questionVariablesDoc
        );

        newQuestionDoc.setQuestion(questionContentDoc);

        // options
        List<OptionDataDoc> optionDataEntities = entity.getOptions().stream()
                .map(this::mapOptionToDocument).toList();

        newQuestionDoc.setOptions(optionDataEntities);

        return newQuestionDoc;
    }

    private OptionDataEntity mapOptionToEntity(OptionDataDoc optionDataDoc) {
        OptionDataEntity optionDataEntity = new OptionDataEntity();
        optionDataEntity.setTemplate(optionDataDoc.getTemplate());
        optionDataEntity.setTemplate(optionDataDoc.getTemplate());
        return optionDataEntity;
    }

    private OptionDataDoc mapOptionToDocument(OptionDataEntity optionDataEntity) {
        OptionDataDoc optionDataDoc = new OptionDataDoc();
        optionDataDoc.setTemplate(optionDataEntity.getTemplate());
        optionDataDoc.setTemplate(optionDataEntity.getTemplate());
        return optionDataDoc;
    }
}
