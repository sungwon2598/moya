package com.study.moya.admin.controller;

import com.study.moya.admin.dto.roadmap.request.WorksheetUpdateRequest;
import com.study.moya.admin.dto.roadmap.response.WorksheetResponse;
import com.study.moya.admin.service.AdminWorksheetService;
import com.study.moya.ai_roadmap.dto.request.WorkSheetRequest;
import com.study.moya.ai_roadmap.dto.response.WorksheetStatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/worksheets")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminWorksheetController {

    private final AdminWorksheetService adminWorksheetService;

    @PostMapping("/{roadmapId}/generate")
    public ResponseEntity<Void> generateWorksheets(@PathVariable Long roadmapId, @Valid @RequestBody WorkSheetRequest request) {
        log.info("로드맵 ID: {}의 학습지 생성 시작", roadmapId);
        adminWorksheetService.generateAllWorksheets(roadmapId, request)
                .thenRun(() -> log.info("로드맵 ID: {}의 학습지 생성 완료", roadmapId))
                .exceptionally(ex -> {
                    log.error("학습지 생성 중 오류 발생: {}", ex.getMessage());
                    return null;
                });
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{roadmapId}/worksheets/status")
    public ResponseEntity<WorksheetStatusResponse> getWorksheetStatus(@PathVariable Long roadmapId) {
        long progress = adminWorksheetService.getWorksheetProgress(roadmapId);
        boolean isCompleted = progress >= 100;

        WorksheetStatusResponse response = new WorksheetStatusResponse(
                isCompleted,
                progress,
                isCompleted ? "완료" : "진행 중"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roadmapId}")
    public ResponseEntity<List<WorksheetResponse>> getAllWorksheets(@PathVariable Long roadmapId) {
        log.info("로드맵 ID: {}의 모든 워크시트 조회 요청", roadmapId);

        List<WorksheetResponse> worksheets = adminWorksheetService.getAllWorksheetsByRoadmapId(roadmapId);

        log.info("로드맵 ID: {}의 워크시트 {}개 조회 완료", roadmapId, worksheets.size());
        return ResponseEntity.ok(worksheets);
    }

    //특정 워크시트 수정
    @PutMapping("/update/{dailyPlanId}")
    public ResponseEntity<Void> updateWorksheet(
            @PathVariable Long dailyPlanId,
            @Valid @RequestBody WorksheetUpdateRequest request) {
        log.info("일단 api 건들긴 했다!!!!");
        log.info("일간계획 ID: {}의 워크시트 수정 요청", dailyPlanId);

        adminWorksheetService.updateWorksheet(dailyPlanId, request);

        log.info("일간계획 ID: {}의 워크시트 수정 완료", dailyPlanId);
        return ResponseEntity.noContent().build();  // 204로 변경
    }

    //특정 워크시트 삭제 (내용만 null로 변경)
    @DeleteMapping("/delete/{dailyPlanId}")
    public ResponseEntity<Void> deleteWorksheet(@PathVariable Long dailyPlanId) {
        log.info("일간계획 ID: {}의 워크시트 삭제 요청", dailyPlanId);

        adminWorksheetService.deleteWorksheet(dailyPlanId);

        log.info("일간계획 ID: {}의 워크시트 삭제 완료", dailyPlanId);
        return ResponseEntity.noContent().build();
    }
}
