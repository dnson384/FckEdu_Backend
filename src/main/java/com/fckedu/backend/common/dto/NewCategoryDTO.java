package com.fckedu.backend.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewCategoryDTO {
    private String subject;
    private String chapter;
    private List<NewLessonDataDTO> lessons;

    public NewCategoryDTO(String subject, String chapter, List<NewLessonDataDTO> lessons) {
        this.subject = subject;
        this.chapter = chapter;
        this.lessons = lessons;
    }
}
