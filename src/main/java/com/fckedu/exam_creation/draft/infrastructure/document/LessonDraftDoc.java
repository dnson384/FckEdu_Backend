package com.fckedu.exam_creation.draft.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDraftDoc {
    @Builder.Default
    private ObjectId id = new ObjectId();

    private String name;

    @Builder.Default
    private List<MatrixItemDoc> matrix = new ArrayList<>();

    @Builder.Default
    private List<MatrixDetailItemDoc> matrixDetails = new ArrayList<>();
}
