package com.fckedu.backend.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStatDTO {
    private String exerciseType;
    private List<String> difficultyLevels;
    private List<String> learningOutcomes;
    private String questionType;
    private Integer count;
}
