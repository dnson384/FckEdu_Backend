package com.fckedu.backend.category.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Categories")
public class CategoryDocument {
    @Id
    private String id;

    private String subject;
    private String chapter;

    @Builder.Default
    private List<LessonDataDoc> lessons = new ArrayList<>();

    @CreatedDate
    private Instant createAt;

    @LastModifiedDate
    private Instant updatedAt;
}
