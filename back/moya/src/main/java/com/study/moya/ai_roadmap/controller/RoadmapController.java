package com.study.moya.ai_roadmap.controller;

import com.study.moya.ai_roadmap.dto.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.WeeklyRoadmapResponse;
import com.study.moya.ai_roadmap.service.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/generate")
    public CompletableFuture<ResponseEntity<WeeklyRoadmapResponse>> generateWeeklyRoadmap(
            @Valid @RequestBody RoadmapRequest request
    ) {
        log.info("작동해버리기~============================");
        return roadmapService.generateWeeklyRoadmapAsync(request)
                .thenApply(response -> ResponseEntity.ok(response))
                .exceptionally(ex -> {
                    // 예외 발생 시 처리
                    return ResponseEntity.status(500).body(null);
                });
    }
}
