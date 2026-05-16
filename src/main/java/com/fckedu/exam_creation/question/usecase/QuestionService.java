package com.fckedu.exam_creation.question.usecase;

import com.fckedu.exam_creation.common.dto.question.NewQuestionDTO;
import com.fckedu.exam_creation.question.domain.entity.QuestionEntity;
import com.fckedu.exam_creation.question.domain.repository.IQuestionRepository;
import com.fckedu.exam_creation.question.dto.mapper.QuestionDTOMapper;
import com.fckedu.exam_creation.question.dto.request.ExamMatrixDetailDTO;
import com.fckedu.exam_creation.question.dto.response.ExamQuestionsDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionService {
    private final IQuestionRepository repo;
    private final QuestionDTOMapper mapper;

    public QuestionService(IQuestionRepository repo, QuestionDTOMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public void insert(List<NewQuestionDTO> questions) {
        List<QuestionEntity> newQuestionsEntity = questions.stream().map(mapper::newQuestionDTOToEntity).toList();
        repo.saveQuestions(newQuestionsEntity);
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
}
