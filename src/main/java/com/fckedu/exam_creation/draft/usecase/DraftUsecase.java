package com.fckedu.exam_creation.draft.usecase;

import com.fckedu.exam_creation.category.usecase.CategoryService;
import com.fckedu.exam_creation.common.dto.category.response.CategoryResponseDTO;
import com.fckedu.exam_creation.common.dto.category.response.LessonDataResponseDTO;
import com.fckedu.exam_creation.common.exception.NotFoundException;
import com.fckedu.exam_creation.draft.domain.entity.DraftEntity;
import com.fckedu.exam_creation.draft.domain.entity.LessonDraftEntity;
import com.fckedu.exam_creation.draft.domain.entity.MatrixItemEntity;
import com.fckedu.exam_creation.draft.domain.payload.UpdateChaptersPayload;
import com.fckedu.exam_creation.draft.domain.payload.UpdateLessonsPayload;
import com.fckedu.exam_creation.draft.domain.payload.UpdateMatrixPayload;
import com.fckedu.exam_creation.draft.domain.payload.UpdateParam;
import com.fckedu.exam_creation.draft.domain.repository.IDraftRepository;
import com.fckedu.exam_creation.draft.dto.request.CreateDraftDTO;
import com.fckedu.exam_creation.draft.dto.request.UpdateChaptersDraftDTO;
import com.fckedu.exam_creation.draft.dto.request.UpdateLessonsDraftDTO;
import com.fckedu.exam_creation.draft.dto.response.*;
import com.fckedu.exam_creation.draft.usecase.util.DraftUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DraftUsecase {
    private final IDraftRepository repo;
    private final CategoryService categoryService;
    private final DraftUtil util;

    public DraftUsecase(IDraftRepository repo, CategoryService categoryService, DraftUtil util) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.util = util;
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

    public boolean generateMatrix(String draftId) {
        DraftDTO draft = getDraft(draftId);

        if (util.hasMatrix(draft.getChapters(), draft.getQuestionsCount())) {
            return true;
        }

        List<String> chapterIds = draft.getChapters().stream()
                .map(ChapterDraftDTO::getId).toList();
        List<CategoryResponseDTO> categories = categoryService.getByIds(chapterIds);
        Map<String, CategoryResponseDTO> cateMap = categories.stream()
                .collect(Collectors.toMap(CategoryResponseDTO::getId, category -> category));

        Map<String, List<LessonDataResponseDTO>> lessonsData = new HashMap<>();

        for (ChapterDraftDTO chapter : draft.getChapters()) {
            List<String> lessonIds = chapter.getLessons().stream()
                    .map(LessonDraftDTO::getId)
                    .toList();
            CategoryResponseDTO curChapter = cateMap.get(chapter.getId());

            if (curChapter == null) {
                throw new NotFoundException("Chương không tồn tại");
            }

            List<LessonDataResponseDTO> curLessons = curChapter.getLessons().stream()
                    .filter(lesson -> lessonIds.contains(lesson.getId()))
                    .toList();

            lessonsData.put(chapter.getId(), curLessons);
        }

        if (lessonsData.isEmpty()) {
            throw new NotFoundException("Nội dung không tồn tại");
        }

        List<ChapterDraftDTO> newDraftChapters = new ArrayList<>(draft.getChapters());
        util.generateMatrix(
                lessonsData,
                newDraftChapters,
                draft.getQuestionTypes(),
                draft.getQuestionsCount()
        );

        List<UpdateMatrixPayload> payload = new ArrayList<>();
        for (ChapterDraftDTO chapter : newDraftChapters) {
            for (LessonDraftDTO lesson : chapter.getLessons()) {
                payload.add(new UpdateMatrixPayload(
                        draftId,
                        chapter.getId(),
                        lesson.getId(),
                        lesson.getMatrix().stream()
                                .map(m -> new MatrixItemEntity(
                                        m.getQuestionType(),
                                        m.getDifficultyLevel(),
                                        m.getSelectedCount()
                                ))
                                .toList()
                ));
            }
        }

        return repo.updateMatrix(payload);
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
