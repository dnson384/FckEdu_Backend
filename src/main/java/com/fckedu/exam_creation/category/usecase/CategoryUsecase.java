package com.fckedu.exam_creation.category.usecase;

import com.fckedu.exam_creation.category.domain.entity.CategoryEntity;
import com.fckedu.exam_creation.category.domain.entity.LessonDataEntity;
import com.fckedu.exam_creation.category.domain.repository.ICategoryRepository;
import com.fckedu.exam_creation.category.dto.response.BankStatDTO;
import com.fckedu.exam_creation.category.dto.response.CategoryDTO;
import com.fckedu.exam_creation.category.dto.response.LessonDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryUsecase {
    private final ICategoryRepository repo;

    public CategoryUsecase(ICategoryRepository repo) {
        this.repo = repo;
    }

    public List<CategoryDTO> getAll() {
        List<CategoryEntity> categoryEntities = repo.getAll();
        return categoryEntities.stream()
                .map(category -> {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    categoryDTO.setId(category.getId());
                    categoryDTO.setSubject(category.getSubject());
                    categoryDTO.setChapter(category.getChapter());

                    List<LessonDataDTO> lessonDataDTOS = category.getLessons().stream()
                            .map(this::mapDocToDTO)
                            .toList();
                    categoryDTO.setLessons(lessonDataDTOS);

                    return categoryDTO;
                })
                .toList();
    }

    private LessonDataDTO mapDocToDTO(LessonDataEntity lesson) {
        LessonDataDTO lessonDataDTO = new LessonDataDTO();
        lessonDataDTO.setId(lesson.getId());
        lessonDataDTO.setName(lesson.getName());

        List<BankStatDTO> bankStatDTOS = lesson.getBankStats().stream()
                .map(bankStat -> new BankStatDTO(
                        bankStat.getExerciseType(),
                        bankStat.getDifficultyLevels(),
                        bankStat.getLearningOutcomes(),
                        bankStat.getQuestionType(),
                        bankStat.getCount()
                ))
                .toList();
        lessonDataDTO.setBankStats(bankStatDTOS);

        return lessonDataDTO;
    }
}
