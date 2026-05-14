package com.fckedu.exam_creation.question.usecase;

import com.fckedu.exam_creation.common.dto.question.NewQuestionDTO;
import com.fckedu.exam_creation.question.domain.entity.OptionDataEntity;
import com.fckedu.exam_creation.question.domain.entity.QuestionContentEntity;
import com.fckedu.exam_creation.question.domain.entity.QuestionEntity;
import com.fckedu.exam_creation.question.domain.entity.VariablesEntity;
import com.fckedu.exam_creation.question.dto.request.ExamMatrixDetailDTO;
import com.fckedu.exam_creation.question.dto.response.ExamQuestionsDTO;
import com.fckedu.exam_creation.question.infrastructure.repository.QuestionRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<QuestionEntity> findByIds(List<String> ids) {
        return repo.findByIds(ids);
    }

    public List<Object> generateExamQuestions(List<ExamMatrixDetailDTO> matrixDetails) {
        // Tạo mảng lessonId không trùng id
        List<String> uniqueLessonIds = matrixDetails.stream()
                .map(ExamMatrixDetailDTO::getLessonId)
                .distinct()
                .toList();

        if (uniqueLessonIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Lấy toàn bộ câu hỏi
        List<QuestionEntity> allQuestionsInLessons = repo.findByLessonIds(uniqueLessonIds);

        // Tạo pool chứa các câu hỏi có thể dùng
        List<QuestionEntity> availablePool = new ArrayList<>(allQuestionsInLessons);

        Map<String, ExamQuestionsDTO> groupedResult = new LinkedHashMap<>();

        for (ExamMatrixDetailDTO detail : matrixDetails) {
            if (detail.getLimit() <= 0) {
                continue;
            }

            // Lọc câu hỏi khớp với điều kiện
            List<QuestionEntity> matchingQuestions = availablePool.stream()
                    .filter(q -> q.getLessonId().equals(detail.getLessonId()) &&
                            q.getExerciseType().equals(detail.getExerciseType()) &&
                            q.getDifficultyLevel().equals(detail.getDifficultyLevel()) &&
                            q.getQuestionType().equals(detail.getQuestionType()) &&
                            q.getLearningOutcomes().contains(detail.getLearningOutcome())
                    )
                    .toList();

            // Log thiếu câu hỏi
            if (matchingQuestions.size() < detail.getLimit()) {
                System.err.printf("[Thiếu câu hỏi] Yêu cầu %d nhưng chỉ có %d câu phù hợp cho %s",
                        detail.getLimit(), matchingQuestions.size(), detail.getLearningOutcome());
            }

            // Trộn câu hỏi
            Collections.shuffle(new ArrayList<>(matchingQuestions));

            // Lấy lượng câu hỏi cần thiết
            int takeCount = Math.min(detail.getLimit(), matchingQuestions.size());
            List<QuestionEntity> selectedQuestions = matchingQuestions.subList(0, takeCount);

            if (!selectedQuestions.isEmpty()) {
                List<String> selectedIds = selectedQuestions.stream()
                        .map(QuestionEntity::getId)
                        .toList();

                String groupKey = detail.getQuestionType() + "_" + detail.getDifficultyLevel();

                // Gom nhóm
                ExamQuestionsDTO group = groupedResult.
                        computeIfAbsent(groupKey, k -> new ExamQuestionsDTO(
                                detail.getQuestionType(),
                                detail.getDifficultyLevel(),
                                new ArrayList<>()));

                group.getQuestionIds().addAll(selectedIds);
            }
        }
        return new ArrayList<>(groupedResult.values());
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
