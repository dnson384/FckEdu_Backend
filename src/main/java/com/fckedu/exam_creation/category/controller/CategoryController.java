package com.fckedu.exam_creation.category.controller;

import com.fckedu.exam_creation.category.dto.response.CategoryDTO;
import com.fckedu.exam_creation.category.usecase.CategoryUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    private final CategoryUsecase categoryUsecase;

    public CategoryController(CategoryUsecase categoryUsecase) {
        this.categoryUsecase = categoryUsecase;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAll() {
        List<CategoryDTO> result = categoryUsecase.getAll();
        return ResponseEntity.ok(result);
    }
}
