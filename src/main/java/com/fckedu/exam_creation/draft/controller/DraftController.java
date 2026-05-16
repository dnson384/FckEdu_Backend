package com.fckedu.exam_creation.draft.controller;

import com.fckedu.exam_creation.common.dto.draft.response.DraftDTO;
import com.fckedu.exam_creation.draft.dto.request.CreateDraftDTO;
import com.fckedu.exam_creation.draft.dto.request.UpdateChaptersDraftDTO;
import com.fckedu.exam_creation.draft.dto.request.UpdateLessonsDraftDTO;
import com.fckedu.exam_creation.draft.usecase.DraftUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draft")
public class DraftController {
    private final DraftUsecase draftUsecase;

    public DraftController(DraftUsecase draftUsecase) {
        this.draftUsecase = draftUsecase;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDraft(@RequestBody CreateDraftDTO payload) {
        return ResponseEntity.ok(draftUsecase.createDraft(payload));
    }

    @GetMapping("/{draftId}")
    public ResponseEntity<DraftDTO> getDraft(@PathVariable String draftId) {
        return ResponseEntity.ok(draftUsecase.getDraft(draftId));
    }

    @PutMapping("/chapter")
    public ResponseEntity<Boolean> updateChapters(@RequestBody UpdateChaptersDraftDTO payload) {
        return ResponseEntity.ok(draftUsecase.updateChapters(payload));
    }

    @PutMapping("/lesson")
    public ResponseEntity<Boolean> updateLessons(@RequestBody UpdateLessonsDraftDTO payload) {
        return ResponseEntity.ok(draftUsecase.updateLessons(payload));
    }

    @PutMapping("/generate-matrix")
    public ResponseEntity<Boolean> generateMatrix(@RequestParam String draftId) {
        return ResponseEntity.ok(draftUsecase.generateMatrix(draftId));
    }

    @PutMapping("/generate-matrix-details")
    public ResponseEntity<Boolean> generateMatrixDetails(@RequestParam String draftId) {
        return ResponseEntity.ok(draftUsecase.generateMatrixDetails(draftId));
    }
}
