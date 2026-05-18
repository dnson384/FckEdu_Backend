package com.fckedu.exam_creation.exam.dto.response;

import com.fckedu.exam_creation.common.dto.question.response.ContentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailDTO {
    private String id;
    private ContentDTO question;
    private List<ContentDTO> options;
}
