package com.fckedu.backend.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private String id;
    private String subject;
    private String chapter;
    private List<LessonDataDTO> lessons;
}
