package com.fckedu.backend.category.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDataDoc {
    @Field("_id")
    private String id;

    private String name;

    @Builder.Default
    private List<BankStatDoc> bankStats = new ArrayList<>();
}
