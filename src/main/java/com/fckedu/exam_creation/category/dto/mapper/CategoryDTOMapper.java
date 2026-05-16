package com.fckedu.exam_creation.category.dto.mapper;

import com.fckedu.exam_creation.category.domain.entity.CategoryEntity;
import com.fckedu.exam_creation.category.dto.response.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryDTOMapper {
    CategoryDTO entityToDTO(CategoryEntity entity);

    CategoryEntity dtoToEntity(CategoryDTO dto);
}
