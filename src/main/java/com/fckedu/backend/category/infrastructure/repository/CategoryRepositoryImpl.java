package com.fckedu.backend.category.infrastructure.repository;

import com.fckedu.backend.category.domain.entity.CategoryEntity;
import com.fckedu.backend.category.domain.repository.ICategoryRepository;
import com.fckedu.backend.category.infrastructure.document.BankStatDoc;
import com.fckedu.backend.category.infrastructure.document.CategoryDocument;
import com.fckedu.backend.category.infrastructure.document.LessonDataDoc;
import com.fckedu.backend.category.infrastructure.mapper.CategoryMapper;
import com.fckedu.backend.common.dto.category.response.SavedCategoryResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements ICategoryRepository {
    private final MongoTemplate mongoTemplate;
    private final CategoryMapper categoryMapper;

    public CategoryRepositoryImpl(MongoTemplate mongoTemplate, CategoryMapper categoryMapper) {
        this.mongoTemplate = mongoTemplate;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public SavedCategoryResponse saveCategory(CategoryEntity category) {
        CategoryDocument categoryDocument = categoryMapper.toDocument(category);

        // Tìm kiếm chương đã tồn tại
        Query query = new Query(Criteria.where("chapter").is(category.getChapter()));
        CategoryDocument existedChapter = mongoTemplate.findOne(query, CategoryDocument.class);

        if (existedChapter == null) {
            categoryDocument.setCreateAt(LocalDateTime.now());
            categoryDocument.setUpdatedAt(LocalDateTime.now());
            CategoryDocument created = mongoTemplate.save(categoryDocument);

            return new SavedCategoryResponse(created.getId(), created.getLessons().get(0).getId().toString());
        }

        // Tìm bài học trong chương đã tồn tại
        LessonDataDoc newLesson = categoryDocument.getLessons().get(0);
        int existingLessonIndex = -1;

        for (int index = 0; index < existedChapter.getLessons().size(); index++) {
            if (existedChapter.getLessons().get(index).getName().equals(category.getLessons().get(0).getName())) {
                existingLessonIndex = index;
                break;
            }
        }

        String targetLessonId;

        if (existingLessonIndex == -1) {
            existedChapter.getLessons().add(newLesson);
            existedChapter.setUpdatedAt(LocalDateTime.now());
            mongoTemplate.save(existedChapter);

            int lastIndex = existedChapter.getLessons().size() - 1;
            targetLessonId = existedChapter.getLessons().get(lastIndex).getId().toString();
        } else {
            LessonDataDoc existingLesson = existedChapter.getLessons().get(existingLessonIndex);
            List<BankStatDoc> newBankStats = newLesson.getBankStats();

            if (newBankStats != null && !newBankStats.isEmpty()) {
                for (BankStatDoc newStat : newBankStats) {
                    Optional<BankStatDoc> existingStatOpt = existingLesson.getBankStats().stream()
                            .filter(oldStat ->
                                    oldStat.getExcerciseType().equals(newStat.getExcerciseType()) &&
                                            oldStat.getQuestionType().equals(newStat.getQuestionType()) &&
                                            oldStat.getDifficultyLevels().equals(newStat.getDifficultyLevels()) &&
                                            oldStat.getLearningOutcomes().equals(newStat.getLearningOutcomes())
                            ).findFirst();

                    if (existingStatOpt.isPresent()) {
                        BankStatDoc existingStat = existingStatOpt.get();
                        existingStat.setCount(existingStat.getCount() + newStat.getCount());
                    } else {
                        existingLesson.getBankStats().add(newStat);
                    }
                }
                existedChapter.setUpdatedAt(LocalDateTime.now());
                mongoTemplate.save(existedChapter);
            }

            targetLessonId = existingLesson.getId().toString();
        }

        return new SavedCategoryResponse(existedChapter.getId(), targetLessonId);
    }
}
