package com.fckedu.exam_creation.draft.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChaptersPayload {
    String draftId;
    List<UpdateParam> add;
    List<String> del;
}
