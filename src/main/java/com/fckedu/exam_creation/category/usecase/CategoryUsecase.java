package com.fckedu.exam_creation.category.usecase;

import com.fckedu.exam_creation.category.domain.entity.CategoryEntity;
import com.fckedu.exam_creation.category.domain.entity.LessonDataEntity;
import com.fckedu.exam_creation.category.domain.repository.ICategoryRepository;
import com.fckedu.exam_creation.category.dto.mapper.CategoryDTOMapper;
import com.fckedu.exam_creation.category.dto.response.BankStatDTO;
import com.fckedu.exam_creation.category.dto.response.CategoryDTO;
import com.fckedu.exam_creation.category.dto.response.LessonDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryUsecase {
    private final ICategoryRepository repo;
    private final CategoryDTOMapper mapper;

    public CategoryUsecase(ICategoryRepository repo, CategoryDTOMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<CategoryDTO> getAll() {
        List<CategoryEntity> categoryEntities = repo.getAll();
        return categoryEntities.stream()
                .map(mapper::entityToDTO)
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
