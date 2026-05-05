package com.fckedu.backend.question.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentDoc {
    private String template;

    @Builder.Default
    private VariablesDoc variables = new VariablesDoc();
}
