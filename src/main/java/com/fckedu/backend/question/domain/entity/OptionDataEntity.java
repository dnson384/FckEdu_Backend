package com.fckedu.backend.question.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDataEntity {
    private String template;
    private VariablesEntity variables = new VariablesEntity();
}
