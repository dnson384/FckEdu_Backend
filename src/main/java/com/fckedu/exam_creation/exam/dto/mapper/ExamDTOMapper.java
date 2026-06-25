package com.fckedu.exam_creation.exam.dto.mapper;

import com.fckedu.exam_creation.common.dto.question.response.QuestionDTO;
import com.fckedu.exam_creation.exam.domain.entity.ChapterExamEntity;
import com.fckedu.exam_creation.exam.domain.entity.ExamEntity;
import com.fckedu.exam_creation.exam.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExamDTOMapper {
    public ExamDetailDTO convertToExamDetailResponse(ExamEntity exam, List<QuestionDTO> questions) {
        ExamDetailDTO dto = new ExamDetailDTO();
        dto.setId(exam.getId());
        dto.setUserId(exam.getUserId());
        dto.setName(exam.getName());

        List<QuestionDTO> validQuestions = new ArrayList<>();
        for (ChapterExamEntity chapter : exam.getChapters()) {
            List<QuestionDTO> matchedQuestions = questions.stream()
                    .filter(q -> q.getCategoryId().equals(chapter.getId()) &&
                            chapter.getLessonIds().contains(q.getLessonId()))
                    .toList();
            validQuestions.addAll(matchedQuestions);
        }

        List<ExamQuestionDTO> examQuestionDTOS = validQuestions.stream()
                .collect(Collectors.groupingBy(
                        q -> q.getQuestionType() + "-" + q.getDifficultyLevel()
                ))
                .values().stream()
                .map(groupedList -> {
                    QuestionDTO sampleQuestion = groupedList.get(0);

                    List<QuestionDetailDTO> detailDTOs = groupedList.stream()
                            .map(q -> new QuestionDetailDTO(
                                    q.getId(),
                                    q.getQuestion(),
                                    q.getOptions()))
                            .toList();

                    return new ExamQuestionDTO(
                            sampleQuestion.getQuestionType(),
                            sampleQuestion.getDifficultyLevel(),
                            detailDTOs
                    );
                })
                .toList();

        dto.setGroups(examQuestionDTOS);

        return dto;
    }

    public ExamDTO convertToExamResponse(ExamEntity entity) {
        List<ExamChapterDTO> chapterDTOS = entity.getChapters().stream()
                .map(chapter -> new ExamChapterDTO(
                        chapter.getId(),
                        chapter.getLessonIds()
                ))
                .toList();

        Integer questionCount = entity.getQuestions().stream()
                .mapToInt(q -> q.getQuestionIds().size())
                .sum();

        return new ExamDTO(entity.getId(),
                entity.getName(),
                chapterDTOS,
                questionCount);
    }
}
