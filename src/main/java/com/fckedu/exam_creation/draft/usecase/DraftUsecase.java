package com.fckedu.exam_creation.draft.usecase;

import com.fckedu.exam_creation.category.usecase.CategoryService;
import com.fckedu.exam_creation.draft.domain.entity.DraftEntity;
import com.fckedu.exam_creation.draft.domain.entity.LessonDraftEntity;
import com.fckedu.exam_creation.draft.domain.payload.UpdateChaptersPayload;
import com.fckedu.exam_creation.draft.domain.payload.UpdateLessonsPayload;
import com.fckedu.exam_creation.draft.domain.payload.UpdateParam;
import com.fckedu.exam_creation.draft.domain.repository.IDraftRepository;
import com.fckedu.exam_creation.draft.dto.request.CreateDraftDTO;
import com.fckedu.exam_creation.draft.dto.request.UpdateChaptersDraftDTO;
import com.fckedu.exam_creation.draft.dto.request.UpdateLessonsDraftDTO;
import com.fckedu.exam_creation.draft.dto.response.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DraftUsecase {
    private final IDraftRepository repo;
    private final CategoryService categoryService;

    public DraftUsecase(IDraftRepository repo, CategoryService categoryService) {
        this.repo = repo;
        this.categoryService = categoryService;
    }

    public String createDraft(CreateDraftDTO payload) {
        DraftEntity payloadDomain = new DraftEntity();
        payloadDomain.setQuestionsCount(payload.getQuestionsCount());
        payloadDomain.setQuestionTypes(payload.getQuestionTypes());

        return repo.createDraft(payloadDomain);
    }

    public DraftDTO getDraft(String draftId) {
        DraftEntity draft = repo.getDraft(draftId);
        return new DraftDTO(
                draft.getId(),
                draft.getQuestionsCount(),
                draft.getQuestionTypes(),
                draft.getChapters().stream()
                        .map(chapter -> new ChapterDraftDTO(
                                chapter.getId(),
                                chapter.getName(),
                                chapter.getLessons().stream()
                                        .map(this::mapLessonEntityToDTO)
                                        .toList()
                        ))
                        .toList()
        );
    }

    public boolean updateChapters(UpdateChaptersDraftDTO payload) {
        UpdateChaptersPayload payloadDomain = new UpdateChaptersPayload(
                payload.getDraftId(),
                payload.getAdd().stream()
                        .map(item -> new UpdateParam(
                                item.getId(),
                                item.getName()
                        ))
                        .toList(),
                payload.getDel()
        );

        return repo.updateChapters(payloadDomain);
    }

    public boolean updateLessons(UpdateLessonsDraftDTO payload) {
        UpdateLessonsPayload payloadDomain = new UpdateLessonsPayload(
                payload.getDraftId(),
                payload.getChapterId(),
                payload.getAdd().stream()
                        .map(item -> new UpdateParam(
                                item.getId(),
                                item.getName()
                        ))
                        .toList(),
                payload.getDel()
        );

        return repo.updateLessons(payloadDomain);
    }


    private LessonDraftDTO mapLessonEntityToDTO(LessonDraftEntity lesson) {
        LessonDraftDTO lessonDraftDTO = new LessonDraftDTO();
        lessonDraftDTO.setId(lesson.getId());
        lessonDraftDTO.setName(lesson.getName());

        List<MatrixItemDTO> matrixItemDTOS = lesson.getMatrix().stream()
                .map(matrixItemEntity -> new MatrixItemDTO(
                        matrixItemEntity.getQuestionType(),
                        matrixItemEntity.getDifficultyLevel(),
                        matrixItemEntity.getSelectedCount()
                ))
                .toList();
        lessonDraftDTO.setMatrix(matrixItemDTOS);

        List<MatrixDetailItemDTO> matrixDetailItemDTOS = lesson.getMatrixDetails().stream()
                .map(matrixDetailItemEntity -> new MatrixDetailItemDTO(
                        matrixDetailItemEntity.getExerciseType(),
                        matrixDetailItemEntity.getDifficultyLevel(),
                        matrixDetailItemEntity.getLearningOutcome(),
                        matrixDetailItemEntity.getQuestionType(),
                        matrixDetailItemEntity.getSelectedCount()
                ))
                .toList();
        lessonDraftDTO.setMatrixDetails(matrixDetailItemDTOS);

        return lessonDraftDTO;
    }
}
