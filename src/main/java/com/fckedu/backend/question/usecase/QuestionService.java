package com.fckedu.backend.question.usecase;

import com.fckedu.backend.common.dto.question.NewQuestionDTO;
import com.fckedu.backend.question.domain.entity.OptionDataEntity;
import com.fckedu.backend.question.domain.entity.QuestionContentEntity;
import com.fckedu.backend.question.domain.entity.QuestionEntity;
import com.fckedu.backend.question.domain.entity.VariablesEntity;
import com.fckedu.backend.question.infrastructure.repository.QuestionRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepositoryImpl repo;

    public QuestionService(QuestionRepositoryImpl repo) {
        this.repo = repo;
    }

    public boolean insert(List<NewQuestionDTO> questions) {
        List<QuestionEntity> newQuestionsEntity = questions.stream().map(this::mapQuestionToEntity).toList();

        return repo.saveQuestions(newQuestionsEntity);
    }

    private QuestionEntity mapQuestionToEntity(NewQuestionDTO question) {
        QuestionEntity newQuestionEntity = new QuestionEntity();
        newQuestionEntity.setSubject(question.getSubject());
        newQuestionEntity.setChapterId(question.getChapterId());
        newQuestionEntity.setLessonId(question.getLessonId());
        newQuestionEntity.setExerciseType(question.getExerciseType());
        newQuestionEntity.setDifficultyLevel(question.getDifficultyLevel());
        newQuestionEntity.setLearningOutcomes(question.getLearningOutcomes());
        newQuestionEntity.setQuestionType(question.getQuestionType());

        // Question content
        QuestionContentEntity questionContentEntity = new QuestionContentEntity();
        questionContentEntity.setTemplate(question.getQuestion().getTemplate());
        questionContentEntity.setVariables(new VariablesEntity(
                question.getQuestion().getVariables().getMath(),
                question.getQuestion().getVariables().getImage()
        ));
        newQuestionEntity.setQuestion(questionContentEntity);

        // Options
        List<OptionDataEntity> optionDataEntities = question.getOptions().stream()
                .map(option -> {
                    OptionDataEntity optionDataEntity = new OptionDataEntity();
                    optionDataEntity.setTemplate(option.getTemplate());
                    optionDataEntity.setVariables(new VariablesEntity(
                            option.getVariables().getMath(),
                            option.getVariables().getImage()
                    ));
                    return optionDataEntity;
                }).toList();
        newQuestionEntity.setOptions(optionDataEntities);

        return newQuestionEntity;
    }
}
