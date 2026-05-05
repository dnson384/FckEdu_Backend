package com.fckedu.backend.category.infrastructure.mapper;

import com.fckedu.backend.category.domain.entity.BankStatEntity;
import com.fckedu.backend.category.domain.entity.CategoryEntity;
import com.fckedu.backend.category.domain.entity.LessonDataEntity;
import com.fckedu.backend.category.infrastructure.document.BankStatDoc;
import com.fckedu.backend.category.infrastructure.document.CategoryDocument;
import com.fckedu.backend.category.infrastructure.document.LessonDataDoc;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {
    public CategoryEntity toEntity(CategoryDocument raw) {
        if (raw == null) return null;

        CategoryEntity newCategoryEntity = new CategoryEntity();
        newCategoryEntity.setId(raw.getId());
        newCategoryEntity.setSubject(raw.getSubject());
        newCategoryEntity.setChapter(raw.getChapter());

        List<LessonDataEntity> newLessonsDataEntity = raw.getLessons().stream()
                .map(this::mapToLessonEntity).toList();

        newCategoryEntity.setLessons(newLessonsDataEntity);
        return newCategoryEntity;
    }

    public CategoryDocument toDocument(CategoryEntity entity) {
        if (entity == null) return null;

        CategoryDocument newCategoryDoc = new CategoryDocument();
        if (entity.getId() != null && ObjectId.isValid(entity.getId())) {
            newCategoryDoc.setId(entity.getId());
        }

        newCategoryDoc.setSubject(entity.getSubject());
        newCategoryDoc.setChapter(entity.getChapter());

        List<LessonDataDoc> newLessonsDataDoc = entity.getLessons().stream()
                .map(this::mapToLessonDoc).toList();

        newCategoryDoc.setLessons(newLessonsDataDoc);
        return newCategoryDoc;
    }

    private LessonDataEntity mapToLessonEntity(LessonDataDoc lesson) {
        List<BankStatEntity> bankStatEntities = lesson.getBankStats().stream()
                .map(bankStat -> new BankStatEntity(
                        bankStat.getExcerciseType(),
                        bankStat.getDifficultyLevels(),
                        bankStat.getLearningOutcomes(),
                        bankStat.getQuestionType(),
                        bankStat.getCount()
                ))
                .toList();

        return new LessonDataEntity(
                lesson.getId(),
                lesson.getName(),
                bankStatEntities
        );
    }

    private LessonDataDoc mapToLessonDoc(LessonDataEntity lesson) {
        List<BankStatDoc> bankStatDocs = lesson.getBankStats().stream()
                .map(bankStat -> new BankStatDoc(
                        bankStat.getExerciseType(),
                        bankStat.getDifficultyLevels(),
                        bankStat.getLearningOutcomes(),
                        bankStat.getQuestionType(),
                        bankStat.getCount()
                ))
                .toList();

        return new LessonDataDoc(
                lesson.getId(),
                lesson.getName(),
                bankStatDocs
        );
    }
}
