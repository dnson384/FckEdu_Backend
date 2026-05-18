package com.fckedu.exam_creation.exam.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Exams")
public class ExamDocument {
    @Id
    private String id;

    private List<ChapterExamDocument> chapters;
    private List<QuestionExamDocument> questions;
}
