package com.fckedu.exam_creation.draft.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLessonsPayload {
    String draftId;
    String chapterId;
    List<UpdateParam> add;
    List<String> del;
}
