package com.fckedu.backend.category.domain.repository;

import com.fckedu.backend.category.domain.entity.CategoryEntity;
import com.fckedu.backend.common.dto.category.response.SavedCategoryResponse;

public interface ICategoryRepository {
    SavedCategoryResponse saveCategory(CategoryEntity category);
}