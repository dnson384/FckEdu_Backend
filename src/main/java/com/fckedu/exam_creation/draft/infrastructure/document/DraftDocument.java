package com.fckedu.exam_creation.draft.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Drafts")
public class DraftDocument {
    @Id
    private String id;

    private Integer questionsCount;

    @Builder.Default
    private List<String> questionTypes = new ArrayList<>();

    @Builder.Default
    private List<ChapterDraftDocument> chapters = new ArrayList<>();
}
