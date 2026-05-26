package com.fckedu.exam_creation.draft.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftEntity {
    private String id;
    private String examName;
    private Integer questionsCount;
    private List<String> questionTypes;
    private List<ChapterDraftEntity> chapters;
}
