package com.fckedu.exam_creation.category.usecase;

import com.fckedu.exam_creation.category.domain.entity.BankStatEntity;
import com.fckedu.exam_creation.category.domain.entity.CategoryEntity;
import com.fckedu.exam_creation.category.domain.entity.LessonDataEntity;
import com.fckedu.exam_creation.category.domain.repository.ICategoryRepository;
import com.fckedu.exam_creation.common.dto.category.NewCategoryDTO;
import com.fckedu.exam_creation.common.dto.category.NewLessonDataDTO;
import com.fckedu.exam_creation.common.dto.category.response.BankStatResponseDTO;
import com.fckedu.exam_creation.common.dto.category.response.CategoryResponseDTO;
import com.fckedu.exam_creation.common.dto.category.response.LessonDataResponseDTO;
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

    public List<CategoryResponseDTO> getByIds(List<String> chapterIds) {
        List<CategoryEntity> categories = repo.getByIds(chapterIds);

        return categories.stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getSubject(),
                        category.getChapter(),
                        category.getLessons().stream()
                                .map(this::mapLessonEntityToDTO)
                                .toList())
                )
                .toList();
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

    private LessonDataResponseDTO mapLessonEntityToDTO(LessonDataEntity lesson) {
        LessonDataResponseDTO lessonDTO = new LessonDataResponseDTO();
        lessonDTO.setId(lesson.getId());
        lessonDTO.setName(lesson.getName());

        List<BankStatResponseDTO> bankStatDTO = lessonDTO.getBankStats().stream()
                .map(bankStat -> new BankStatResponseDTO(
                        bankStat.getExerciseType(),
                        bankStat.getDifficultyLevels(),
                        bankStat.getLearningOutcomes(),
                        bankStat.getQuestionType(),
                        bankStat.getCount()
                ))
                .toList();

        lessonDTO.setBankStats(bankStatDTO);
        return lessonDTO;
    }
}
