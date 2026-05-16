package com.fckedu.exam_creation.draft.mapper;

import com.fckedu.exam_creation.common.dto.draft.response.DraftDTO;
import com.fckedu.exam_creation.draft.domain.entity.DraftEntity;
import com.fckedu.exam_creation.draft.infrastructure.document.DraftDocument;
import org.mapstruct.Mapper;

//@Component
@Mapper(componentModel = "spring")
public interface DraftMapper {
    DraftEntity docToEntity(DraftDocument draftDocument);

    DraftDocument entityToDocument(DraftEntity draftEntity);

    DraftDTO entityToDTO(DraftEntity draftEntity);
//    public DraftEntity toEntity(DraftDocument raw) {
//        List<ChapterDraftEntity> chapterDraftEntities = raw.getChapters().stream()
//                .map(chapter -> new ChapterDraftEntity(
//                                chapter.getId().toString(),
//                                chapter.getName(),
//                                chapter.getLessons().stream()
//                                        .map(this::mapLessonDocToEntity)
//                                        .toList()
//                        )
//                )
//                .toList();
//
//        return new DraftEntity(
//                raw.getId(),
//                raw.getQuestionsCount(),
//                raw.getQuestionTypes(),
//                chapterDraftEntities
//        );
//    }
//
//    public DraftDocument toDocument(DraftEntity entity) {
//        List<ChapterDraftDoc> chapterDraftDocs = entity.getChapters().stream()
//                .map(chapter -> new ChapterDraftDoc(
//                        new ObjectId(chapter.getId()),
//                        chapter.getName(),
//                        chapter.getLessons().stream()
//                                .map(this::mapLessonEntityToDoc)
//                                .toList()
//                ))
//                .toList();
//
//        return new DraftDocument(
//                entity.getId(),
//                entity.getQuestionsCount(),
//                entity.getQuestionTypes(),
//                chapterDraftDocs
//        );
//    }
//
//    private LessonDraftEntity mapLessonDocToEntity(LessonDraftDoc lesson) {
//        LessonDraftEntity lessonDraftEntity = new LessonDraftEntity();
//        lessonDraftEntity.setId(lesson.getId().toString());
//        lessonDraftEntity.setName(lesson.getName());
//
//        List<MatrixItemEntity> matrixItemEntity = lesson.getMatrix().stream()
//                .map(matrixItemDoc -> new MatrixItemEntity(
//                        matrixItemDoc.getQuestionType(),
//                        matrixItemDoc.getDifficultyLevel(),
//                        matrixItemDoc.getSelectedCount()
//                ))
//                .toList();
//        lessonDraftEntity.setMatrix(matrixItemEntity);
//
//        List<MatrixDetailItemEntity> matrixDetailItemEntities = lesson.getMatrixDetails().stream()
//                .map(matrixDetailItemDoc -> new MatrixDetailItemEntity(
//                        matrixDetailItemDoc.getExerciseType(),
//                        matrixDetailItemDoc.getDifficultyLevel(),
//                        matrixDetailItemDoc.getLearningOutcome(),
//                        matrixDetailItemDoc.getQuestionType(),
//                        matrixDetailItemDoc.getSelectedCount()
//                ))
//                .toList();
//        lessonDraftEntity.setMatrixDetails(matrixDetailItemEntities);
//
//        return lessonDraftEntity;
//    }
//
//    private LessonDraftDoc mapLessonEntityToDoc(LessonDraftEntity lesson) {
//        LessonDraftDoc lessonDraftDoc = new LessonDraftDoc();
//        lessonDraftDoc.setId(new ObjectId(lesson.getId()));
//        lessonDraftDoc.setName(lesson.getName());
//
//        List<MatrixItemDoc> matrixItemDocs = lesson.getMatrix().stream()
//                .map(matrixItemEntity -> new MatrixItemDoc(
//                        matrixItemEntity.getQuestionType(),
//                        matrixItemEntity.getDifficultyLevel(),
//                        matrixItemEntity.getSelectedCount()
//                ))
//                .toList();
//        lessonDraftDoc.setMatrix(matrixItemDocs);
//
//        List<MatrixDetailItemDoc> matrixDetailItemDocs = lesson.getMatrixDetails().stream()
//                .map(matrixDetailItemEntity -> new MatrixDetailItemDoc(
//                        matrixDetailItemEntity.getExerciseType(),
//                        matrixDetailItemEntity.getDifficultyLevel(),
//                        matrixDetailItemEntity.getLearningOutcome(),
//                        matrixDetailItemEntity.getQuestionType(),
//                        matrixDetailItemEntity.getSelectedCount()
//                ))
//                .toList();
//        lessonDraftDoc.setMatrixDetails(matrixDetailItemDocs);
//
//        return lessonDraftDoc;
//    }
}
