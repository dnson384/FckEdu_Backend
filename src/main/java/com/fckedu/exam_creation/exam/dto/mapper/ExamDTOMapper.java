package com.fckedu.exam_creation.exam.dto.mapper;

import com.fckedu.exam_creation.common.dto.question.response.QuestionDTO;
import com.fckedu.exam_creation.exam.domain.entity.ChapterExamEntity;
import com.fckedu.exam_creation.exam.domain.entity.ExamEntity;
import com.fckedu.exam_creation.exam.dto.response.ExamDetailDTO;
import com.fckedu.exam_creation.exam.dto.response.ExamQuestionDTO;
import com.fckedu.exam_creation.exam.dto.response.QuestionDetailDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExamDTOMapper {
    public ExamDetailDTO convertToExamResponse(ExamEntity exam, List<QuestionDTO> questions) {
        ExamDetailDTO dto = new ExamDetailDTO();
        dto.setId(exam.getId());
        dto.setName(exam.getName());

        List<QuestionDTO> validQuestions = new ArrayList<>();
        for (ChapterExamEntity chapter : exam.getChapters()) {
            List<QuestionDTO> matchedQuestions = questions.stream()
                    .filter(q -> q.getChapterId().equals(chapter.getId()) &&
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
}
