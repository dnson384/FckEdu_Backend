package com.fckedu.exam_creation.draft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrixDetailItemDTO {
    String exerciseType;
    String difficultyLevel;
    String learningOutcome;
    String questionType;
    Integer selectedCount;
}
