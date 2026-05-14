package com.fckedu.exam_creation.category.usecase;

import com.fckedu.exam_creation.category.domain.entity.BankStatEntity;
import com.fckedu.exam_creation.category.domain.entity.CategoryEntity;
import com.fckedu.exam_creation.category.domain.entity.LessonDataEntity;
import com.fckedu.exam_creation.category.domain.repository.ICategoryRepository;
import com.fckedu.exam_creation.common.dto.category.NewCategoryDTO;
import com.fckedu.exam_creation.common.dto.category.NewLessonDataDTO;
import com.fckedu.exam_creation.common.dto.category.response.SavedCategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final ICategoryRepository repo;

    public CategoryService(ICategoryRepository repo) {
        this.repo = repo;
    }

    public SavedCategoryResponse insert(NewCategoryDTO category) {
        CategoryEntity newCategoryEntity = new CategoryEntity(
                null,
                category.getSubject(),
                category.getChapter(),
                category.getLessons().stream().map(this::mapLessonToEntity).toList()
        );

        return repo.saveCategory(newCategoryEntity);
    }

    public List<CategoryEntity> getByIds(List<String> chapterIds) {
        return repo.getByIds(chapterIds);
    }

    private LessonDataEntity mapLessonToEntity(NewLessonDataDTO lesson) {
        LessonDataEntity lessonDataEntity = new LessonDataEntity();
        lessonDataEntity.setName(lesson.getName());

        List<BankStatEntity> bankStatEntity = lesson.getBankStats().stream()
                .map(bankStat -> {
                    BankStatEntity entity = new BankStatEntity();
                    entity.setExerciseType(bankStat.getExerciseType());
                    entity.setDifficultyLevels(bankStat.getDifficultyLevels());
                    entity.setLearningOutcomes(bankStat.getLearningOutcomes());
                    entity.setQuestionType(bankStat.getQuestionType());
                    entity.setCount(bankStat.getCount());
                    return entity;
                }).toList();

        lessonDataEntity.setBankStats(bankStatEntity);
        return lessonDataEntity;
    }
}
