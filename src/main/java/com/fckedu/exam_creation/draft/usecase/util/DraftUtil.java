package com.fckedu.exam_creation.draft.usecase.util;

import com.fckedu.exam_creation.common.dto.category.response.BankStatResponseDTO;
import com.fckedu.exam_creation.common.dto.category.response.CategoryResponseDTO;
import com.fckedu.exam_creation.common.dto.category.response.LessonDataResponseDTO;
import com.fckedu.exam_creation.common.dto.draft.response.ChapterDraftDTO;
import com.fckedu.exam_creation.common.dto.draft.response.LessonDraftDTO;
import com.fckedu.exam_creation.common.dto.draft.response.MatrixDetailItemDTO;
import com.fckedu.exam_creation.common.dto.draft.response.MatrixItemDTO;
import com.fckedu.exam_creation.common.exception.NotFoundException;
import com.fckedu.exam_creation.draft.usecase.util.dto.BankPool;
import com.fckedu.exam_creation.draft.usecase.util.dto.Candidate;
import com.fckedu.exam_creation.draft.usecase.util.dto.RemainderItem;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.IntStream;

@Component
public class DraftUtil {
    private final ObjectMapper objectMapper;

    public DraftUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean hasMatrix(
            List<ChapterDraftDTO> chapterData,
            Integer questionsCount
    ) {
        int currentTotalQuestions = 0;

        for (ChapterDraftDTO chapter : chapterData) {
            for (LessonDraftDTO lesson : chapter.getLessons()) {
                if (lesson.getMatrix() != null && !lesson.getMatrix().isEmpty()) {
                    for (MatrixItemDTO matrixItem : lesson.getMatrix()) {
                        currentTotalQuestions += matrixItem.getSelectedCount() != null
                                ? matrixItem.getSelectedCount() : 0;
                    }
                }
            }
        }

        return currentTotalQuestions >= questionsCount;
    }

    public boolean hasMatrixDetail(List<ChapterDraftDTO> chapterData, Integer questionsCount) {
        int currentTotalQuestions = 0;

        for (ChapterDraftDTO chapter : chapterData) {
            for (LessonDraftDTO lesson : chapter.getLessons()) {
                if (lesson.getMatrixDetails() != null && !lesson.getMatrixDetails().isEmpty()) {
                    for (MatrixDetailItemDTO detailItem : lesson.getMatrixDetails()) {
                        currentTotalQuestions += detailItem.getSelectedCount() != null ?
                                detailItem.getSelectedCount() : 0;
                    }
                }
            }
        }

        return currentTotalQuestions >= questionsCount;
    }

    public void generateMatrix(
            Map<String, List<LessonDataResponseDTO>> lessonsData,
            List<ChapterDraftDTO> newDraftChapters,
            List<String> questionTypes,
            Integer questionsCount
    ) {
        List<String> levels = new ArrayList<String>();
        levels.add("Nhận biết");
        levels.add("Thông hiểu");
        levels.add("Vận dụng");
        levels.add("Vận dụng cao");

        Map<String, Integer> neededByType = new HashMap<>();
        createMatrixConfig(questionTypes, questionsCount, neededByType);
        Map<String, Integer> neededByLevel = createLevelConfig(questionsCount);

        List<BankPool> availablePools = flatLessons(lessonsData);

        for (ChapterDraftDTO chapter : newDraftChapters) {
            for (LessonDraftDTO lesson : chapter.getLessons()) {
                lesson.setMatrix(new ArrayList<>());

                for (String type : questionTypes) {
                    for (String level : levels) {
                        lesson.getMatrix().add(
                                new MatrixItemDTO(type, level, 0)
                        );
                    }
                }
            }
        }

        int totalAllocated = 0;
        int maxAttempts = questionsCount * 10;
        int attempts = 0;

        while (totalAllocated < questionsCount && attempts < maxAttempts) {
            attempts++;

            String targetType = neededByType.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            String targetLevel = neededByLevel.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            String lessonName = "";

            if (targetType != null && targetLevel != null) break;

            int matchingPoolIndex = IntStream.range(0, availablePools.size())
                    .filter(i -> {
                        BankPool pool = availablePools.get(i);

                        return pool.getAvailable() > 0
                                && pool.getType().equals(targetType)
                                && pool.getLevel().equals(targetLevel);
                    })
                    .findFirst()
                    .orElse(-1);

            if (matchingPoolIndex != -1) {
                BankPool pool = availablePools.get(matchingPoolIndex);
                pool.setAvailable(pool.getAvailable() - 1);
                neededByType.put(targetType, neededByType.get(targetType) - 1);
                neededByLevel.put(targetLevel, neededByLevel.get(targetLevel) - 1);
                totalAllocated += 1;

                ChapterDraftDTO chapter = newDraftChapters.stream()
                        .filter(c -> c.getId().equals(pool.getChapterId()))
                        .findFirst()
                        .orElse(null);

                assert chapter != null;
                LessonDraftDTO lesson = chapter.getLessons().stream()
                        .filter(l -> l.getId().equals(pool.getLessonId()))
                        .findFirst()
                        .orElse(null);

                if (lesson != null) {
                    lessonName = lesson.getName();
                    MatrixItemDTO existingMatrixItem = lesson.getMatrix().stream()
                            .filter(m -> m.getQuestionType().equals(targetType) &&
                                    m.getDifficultyLevel().equals(targetLevel)
                            )
                            .findFirst()
                            .orElse(null);

                    if (existingMatrixItem != null) {
                        existingMatrixItem.setSelectedCount(existingMatrixItem.getSelectedCount() + 1);
                    }
                }
            } else {
                System.err.printf("Kho thiếu câu %s - %s - %s. Có thể do không đủ lượng câu hỏi",
                        lessonName, targetType, targetLevel);

                neededByLevel.put(targetLevel, neededByLevel.get(targetLevel) - 1);
                neededByType.put(targetType, neededByType.get(targetType) - 1);
            }
        }

        if (totalAllocated < questionsCount) {
            throw new NotFoundException("");
        }
    }

    public void generateMatrixDetails(
            List<CategoryResponseDTO> categories,
            List<LessonDraftDTO> allDraftLessons
    ) {
        Map<String, LessonDataResponseDTO> cateMap = new HashMap<>();
        for (CategoryResponseDTO cate : categories) {
            for (LessonDataResponseDTO lesson : cate.getLessons()) {
                cateMap.put(lesson.getId(), lesson);
            }
        }

        for (LessonDraftDTO lessonDraft : allDraftLessons) {
            LessonDataResponseDTO lessonData = cateMap.get(lessonDraft.getId());
            if (lessonData == null || lessonData.getBankStats().isEmpty()) {
                continue;
            }

            List<MatrixDetailItemDTO> matrixDetailItems = new ArrayList<>();

            List<String> allQuestionTypes = lessonDraft.getMatrix().stream()
                    .map(MatrixItemDTO::getQuestionType)
                    .distinct()
                    .toList();

            Set<String> uniqueCombos = new HashSet<>();
            for (BankStatResponseDTO bankStat : lessonData.getBankStats()) {
                for (String difficultyLevel : bankStat.getDifficultyLevels()) {
                    for (String learningOutcome : bankStat.getLearningOutcomes()) {
                        Map<String, String> combo = Map.of(
                                "e", bankStat.getExerciseType(),
                                "d", difficultyLevel,
                                "o", learningOutcome
                        );

                        String key = objectMapper.writeValueAsString(combo);
                        uniqueCombos.add(key);
                    }
                }
            }

            for (String key : uniqueCombos) {
                JsonNode combo = objectMapper.readTree(key);

                String exerciseType = combo.get("e").asString();
                String difficultyLevel = combo.get("d").asString();
                String learningOutcome = combo.get("o").asString();

                for (String questionType : allQuestionTypes) {
                    MatrixDetailItemDTO detail = new MatrixDetailItemDTO();

                    detail.setExerciseType(exerciseType);
                    detail.setDifficultyLevel(difficultyLevel);
                    detail.setLearningOutcome(learningOutcome);
                    detail.setQuestionType(questionType);
                    detail.setSelectedCount(0);

                    matrixDetailItems.add(detail);
                }

                for (MatrixItemDTO matrixItem : lessonDraft.getMatrix()) {
                    List<Candidate> candidates = lessonData.getBankStats().stream()
                            .filter(stat ->
                                    stat.getQuestionType().equals(matrixItem.getQuestionType()) &&
                                            stat.getDifficultyLevels().contains(matrixItem.getDifficultyLevel()) &&
                                            stat.getCount() > 0
                            )
                            .map(m -> new Candidate(m, m.getCount()))
                            .toList();
                    if (candidates.isEmpty()) continue;

                    int needed = matrixItem.getSelectedCount();
                    int index = 0;

                    while (needed > 0) {
                        Candidate candidate = candidates.get(index);

                        if (candidate.getRemaining() > 0) {
                            candidate.setRemaining(candidate.getRemaining() - 1);
                            needed--;

                            String randomOutcome = candidate.getBankStat().getLearningOutcomes()
                                    .get((int) Math.floor(
                                            Math.random() * candidate.getBankStat().getLearningOutcomes().size()
                                    ));

                            matrixDetailItems.stream()
                                    .filter(d ->
                                            d.getExerciseType().equals(candidate.getBankStat().getExerciseType()) &&
                                                    d.getDifficultyLevel().equals(matrixItem.getDifficultyLevel()) &&
                                                    d.getLearningOutcome().equals(randomOutcome) &&
                                                    d.getQuestionType().equals(matrixItem.getQuestionType())
                                    )
                                    .findFirst()
                                    .ifPresent(detailItem ->
                                            detailItem.setSelectedCount(detailItem.getSelectedCount() + 1));

                            index = (index + 1) % candidates.size();

                            if (candidates.stream().allMatch(x -> x.getRemaining().equals(0))) break;
                        }
                    }
                    lessonDraft.setMatrixDetails(matrixDetailItems);
                }
            }
        }
    }

    private void createMatrixConfig(
            List<String> questionTypes,
            Integer questionsCount,
            Map<String, Integer> config
    ) {
        int total = 0;

        for (String type : questionTypes) {
            double percent = 0;

            if (Objects.equals(type, "Nhiều lựa chọn")) {
                percent = 0.3;
            } else if (Objects.equals(type, "Đúng sai")) {
                percent = 0.4;
            } else if (Objects.equals(type, "Trả lời ngắn")) {
                percent = 0.3;
            }

            int count = (int) Math.floor(questionsCount * percent);

            config.put(type, count);
            total += count;
        }

        int remain = questionsCount - total;

        if (remain > 0) {
            String lastType = questionTypes.get(questionTypes.size() - 1);
            config.put(lastType, config.get(lastType) + remain);
        }
    }

    private Map<String, Integer> createLevelConfig(Integer totalQuestions) {
        Map<String, Double> levelRatio = new HashMap<String, Double>();
        levelRatio.put("Nhận biết", 0.4);
        levelRatio.put("Thông hiểu", 0.3);
        levelRatio.put("Vận dụng", 0.2);
        levelRatio.put("Vận dụng cao", 0.1);

        Map<String, Integer> result = new HashMap<>();
        int assigned = 0;

        List<RemainderItem> remainders = new ArrayList<>();

        for (Map.Entry<String, Double> entry : levelRatio.entrySet()) {
            String level = entry.getKey();
            Double ratio = entry.getValue();

            double exact = totalQuestions * ratio;
            int floorValue = (int) Math.floor(exact);

            result.put(level, floorValue);
            assigned += floorValue;

            remainders.add(
                    new RemainderItem(level, exact - floorValue)
            );
        }

        int remain = totalQuestions - assigned;

        remainders.sort(
                (a, b) -> Double.compare(
                        b.getRemainder(),
                        a.getRemainder()
                )
        );

        for (int i = 0; i < remain; i++) {
            String level = remainders.get(i % remainders.size()).getLevel();
            result.put(level, result.get(level) + 1);
        }

        return result;
    }

    private List<BankPool> flatLessons(Map<String, List<LessonDataResponseDTO>> lessonsData) {
        List<BankPool> pools = new ArrayList<>();

        for (Map.Entry<String, List<LessonDataResponseDTO>> entry : lessonsData.entrySet()) {
            String chapterId = entry.getKey();
            List<LessonDataResponseDTO> lessons = entry.getValue();

            for (LessonDataResponseDTO lesson : lessons) {
                for (BankStatResponseDTO bankStat : lesson.getBankStats()) {
                    String type = bankStat.getQuestionType();
                    int count = bankStat.getCount() != null ? bankStat.getCount() : 0;

                    if (count > 0 && !bankStat.getDifficultyLevels().isEmpty()) {
                        int countPerLevel = (int) Math.floor((double) count / bankStat.getDifficultyLevels().size());

                        for (String level : bankStat.getDifficultyLevels()) {
                            pools.add(new BankPool(
                                    chapterId,
                                    lesson.getId(),
                                    type,
                                    level,
                                    countPerLevel
                            ));
                        }
                    }
                }
            }
        }

        return pools;
    }
}
