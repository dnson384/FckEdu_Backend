package com.fckedu.exam_creation.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionsDTO {
    String questionType;
    String difficultyLevel;
    List<String> questionIds;
}
