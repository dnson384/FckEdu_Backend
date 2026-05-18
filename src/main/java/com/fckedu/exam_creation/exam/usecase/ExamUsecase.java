package com.fckedu.exam_creation.exam.usecase;

import com.fckedu.exam_creation.common.dto.draft.response.ChapterDraftDTO;
import com.fckedu.exam_creation.common.dto.draft.response.DraftDTO;
import com.fckedu.exam_creation.common.dto.draft.response.LessonDraftDTO;
import com.fckedu.exam_creation.common.dto.draft.response.MatrixDetailItemDTO;
import com.fckedu.exam_creation.common.dto.exam.response.ExamQuestionGeneratedDTO;
import com.fckedu.exam_creation.common.dto.question.response.QuestionDTO;
import com.fckedu.exam_creation.draft.usecase.DraftService;
import com.fckedu.exam_creation.exam.domain.entity.ChapterExamEntity;
import com.fckedu.exam_creation.exam.domain.entity.ExamEntity;
import com.fckedu.exam_creation.exam.domain.entity.QuestionExamEntity;
import com.fckedu.exam_creation.exam.domain.repository.IExamRepository;
import com.fckedu.exam_creation.exam.dto.mapper.ExamDTOMapper;
import com.fckedu.exam_creation.exam.dto.response.ExamDetailDTO;
import com.fckedu.exam_creation.question.dto.request.ExamMatrixDetailDTO;
import com.fckedu.exam_creation.question.usecase.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamUsecase {
    private final IExamRepository repo;
    private final QuestionService questionService;
    private final DraftService draftService;
    private final ExamDTOMapper mapper;

    public ExamUsecase(IExamRepository repo, QuestionService questionService, DraftService draftService, ExamDTOMapper mapper) {
        this.repo = repo;
        this.questionService = questionService;
        this.draftService = draftService;
        this.mapper = mapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public String generateExam(String draftId) {
        DraftDTO draft = draftService.getDraft(draftId);

        List<ChapterExamEntity> chaptersExam = new ArrayList<>();

        List<ExamMatrixDetailDTO> examMatrixDetailDTOS = new ArrayList<>();
        for (ChapterDraftDTO chapter : draft.getChapters()) {
            List<String> lessonIds = chapter.getLessons().stream().map(LessonDraftDTO::getId).toList();

            chaptersExam.add(new ChapterExamEntity(
                    lessonIds,
                    chapter.getId()
            ));

            for (LessonDraftDTO lesson : chapter.getLessons()) {
                for (MatrixDetailItemDTO matrixDetail : lesson.getMatrixDetails()) {
                    if (matrixDetail.getSelectedCount() > 0) {
                        examMatrixDetailDTOS.add(new ExamMatrixDetailDTO(
                                chapter.getId(),
                                lesson.getId(),
                                matrixDetail.getExerciseType(),
                                matrixDetail.getDifficultyLevel(),
                                matrixDetail.getLearningOutcome(),
                                matrixDetail.getQuestionType(),
                                matrixDetail.getSelectedCount()
                        ));
                    }
                }
            }
        }

        List<ExamQuestionGeneratedDTO> questions = questionService.generateExamQuestions(examMatrixDetailDTOS);

        ExamEntity payload = new ExamEntity(
                null,
                chaptersExam,
                questions.stream()
                        .map(q -> new QuestionExamEntity(
                                q.getQuestionType(),
                                q.getDifficultyLevel(),
                                q.getQuestionIds()
                        ))
                        .toList()
        );

        String savedExamId = repo.saveExam(payload);
        draftService.deleteDraft(savedExamId);
        return savedExamId;
    }

    public ExamDetailDTO getExamById(String examId) {
        ExamEntity exam = repo.getExamById(examId);

        List<String> questionIds = exam.getQuestions().stream()
                .flatMap(questionExam -> questionExam.getQuestionIds().stream())
                .toList();

        List<QuestionDTO> questions = questionService.findByIds(questionIds);

//        List<ExamQuestionDTO> examQuestionDTOS = new ArrayList<>();
//        for (ChapterExamEntity chapter : exam.getChapters()) {
//            List<QuestionDTO> matchedQuestionsChapter = questions.stream()
//                    .filter(question ->
//                            question.getChapterId().equals(chapter.getId()))
//                    .toList();
//
//            List<QuestionDTO> matchedAll = new ArrayList<>();
//            for (String lessonId : chapter.getLessonIds()) {
//                List<QuestionDTO> matched = matchedQuestionsChapter.stream()
//                        .filter(q -> q.getLessonId().equals(lessonId))
//                        .toList();
//                matchedAll.addAll(matched);
//            }
//
//            List<QuestionDetailDTO> detailDTOS = matchedAll.stream()
//                    .map(question -> new QuestionDetailDTO(
//                            question.getId(),
//                            question.getQuestion(),
//                            question.getOptions()))
//                    .toList();
//
//            // Update ExamQuestionDTO
//            for (QuestionDTO question : matchedAll) {
//                int index = IntStream.range(0, examQuestionDTOS.size())
//                        .filter(i ->
//                                examQuestionDTOS.get(i).getQuestionType().equals(question.getQuestionType()) &&
//                                        examQuestionDTOS.get(i).getDifficultyLevel().equals(question.getDifficultyLevel()))
//                        .findFirst()
//                        .orElse(-1);
//
//                if (index == -1) {
//                    examQuestionDTOS.add(new ExamQuestionDTO(
//                            question.getQuestionType(),
//                            question.getDifficultyLevel(),
//                            new ArrayList<>()
//                    ));
//                    examQuestionDTOS.get(examQuestionDTOS.size() - 1).getQuestions().add(
//                            new QuestionDetailDTO(
//                                    question.getId(), question.getQuestion(), question.getOptions()
//                            )
//                    );
//                } else {
//                    examQuestionDTOS.get(index).getQuestions().add(
//                            new QuestionDetailDTO(
//                                    question.getId(), question.getQuestion(), question.getOptions()
//                            )
//                    );
//                }
//            }
//        }

        return mapper.convertToExamResponse(exam, questions);
    }
}
