package com.fckedu.exam_creation.draft.domain.payload;

import com.fckedu.exam_creation.draft.domain.entity.MatrixDetailItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMatrixDetailsPayload {
    String draftId;
    String chapterId;
    String lessonId;
    List<MatrixDetailItemEntity> matrixDetails;
}
