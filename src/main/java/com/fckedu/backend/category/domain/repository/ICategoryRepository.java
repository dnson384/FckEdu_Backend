package com.fckedu.backend.category.domain.repository;

import com.fckedu.backend.category.domain.entity.CategoryEntity;
import com.fckedu.backend.common.dto.category.response.SavedCategoryResponse;

import java.util.List;

public interface ICategoryRepository {
    SavedCategoryResponse saveCategory(CategoryEntity category);

    List<CategoryEntity> getAll();

    CategoryEntity getById(String chapterId);

    List<CategoryEntity> getByIds(List<String> chapterIds);
}