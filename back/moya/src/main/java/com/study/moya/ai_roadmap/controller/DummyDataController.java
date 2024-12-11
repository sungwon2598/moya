package com.study.moya.ai_roadmap.controller;

import com.study.moya.ai_roadmap.service.DummyDataGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/dummy-data")
@RequiredArgsConstructor
@Tag(name = "Dummy Data Generator", description = "더미 데이터 생성 API")
public class DummyDataController {

    private final DummyDataGeneratorService dummyDataGeneratorService;

    @PostMapping("/generate")
    @Operation(summary = "더미 데이터 생성", description = "20만개의 데일리 플랜 데이터를 생성합니다")
    public ResponseEntity<String> generateDummyData() {
        try {
            dummyDataGeneratorService.generateDummyData();
            return ResponseEntity.ok("20만개의 데일리 플랜 생성이 완료되었습니다.");
        } catch (Exception e) {
            log.error("더미 데이터 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("데이터 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}