package com.fckedu.backend.category.infrastructure.document;

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
public class LessonDataDoc {
    @Builder.Default
    private ObjectId id = new ObjectId();

    private String name;

    @Builder.Default
    private List<BankStatDoc> bankStats = new ArrayList<>();
}
