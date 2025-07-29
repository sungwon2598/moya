package com.study.moya.admin.controller;

import com.study.moya.admin.service.AdminCategoryService;
import com.study.moya.ai_roadmap.dto.request.CreateCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.UpdateCategoryRequest;
import com.study.moya.ai_roadmap.dto.response.CategoryHierarchyResponse;
import com.study.moya.ai_roadmap.dto.response.CategoryResponse;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Admin Category", description = "관리자 카테고리 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/category")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;
    //깃허브 액션 테스트를 위한 주석 추가 시도 4

    // ==================== 조회 API ====================

    @Operation(summary = "전체 카테고리 계층 구조 조회", description = "대분류와 중분류로 구성된 전체 카테고리 계층 구조를 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "카테고리 계층 조회 성공", value = List.class)
    @GetMapping("/hierarchy")
    public ResponseEntity<List<CategoryHierarchyResponse>> getCategoryHierarchy() {
        return ResponseEntity.ok(adminCategoryService.getCategoryHierarchy());
    }

    @Operation(summary = "대분류 카테고리 조회", description = "모든 대분류 카테고리를 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "대분류 조회 성공", value = List.class)
    @GetMapping("/main/list")
    public ResponseEntity<List<CategoryResponse>> getMainCategories() {
        return ResponseEntity.ok(adminCategoryService.getMainCategories());
    }

    @Operation(summary = "하위 카테고리 조회", description = "특정 상위 카테고리의 하위 카테고리들을 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "하위 카테고리 조회 성공", value = List.class)
    @GetMapping("/sub/list/{parentId}")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(
            @Parameter(description = "상위 카테고리 ID", example = "1")
            @PathVariable Long parentId) {
        return ResponseEntity.ok(adminCategoryService.getSubCategories(parentId));
    }

    @Operation(summary = "깊이별 카테고리 조회", description = "특정 깊이의 모든 카테고리를 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "깊이별 카테고리 조회 성공", value = List.class)
    @GetMapping("/depth/{depth}/list")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByDepth(
            @Parameter(description = "카테고리 깊이 (0: 대분류, 1: 중분류, 2: 소분류)", example = "0")
            @PathVariable int depth) {
        List<CategoryResponse> categories = adminCategoryService.getCategoriesByDepth(depth);
        return ResponseEntity.ok(categories);
    }

    // ==================== 생성 API ====================

    @Operation(summary = "대분류 카테고리 생성", description = "새로운 대분류 카테고리를 생성합니다")
    @SwaggerSuccessResponse(status = 201, name = "대분류 생성 성공")
    @PostMapping("/main/new")
    public ResponseEntity<Void> createMainCategory(
            @Parameter(description = "대분류 생성 요청")
            @RequestBody CreateCategoryRequest request) {
        // parentId를 null로 강제 설정
        CreateCategoryRequest mainCategoryRequest = new CreateCategoryRequest(request.getName(), null);
        Long id = adminCategoryService.createCategory(mainCategoryRequest);
        return ResponseEntity.created(URI.create("/api/admin/category/main/" + id)).build();
    }

    @Operation(summary = "중분류 카테고리 생성", description = "기존 대분류 하위에 중분류 카테고리를 생성합니다")
    @SwaggerSuccessResponse(status = 201, name = "중분류 생성 성공")
    @PostMapping("/sub/new")
    public ResponseEntity<Void> createSubCategory(
            @Parameter(description = "중분류 생성 요청 (parentId 필수)")
            @RequestBody CreateCategoryRequest request) {
        if (request.getParentId() == null) {
            throw new IllegalArgumentException("중분류 생성 시 상위 카테고리 ID는 필수입니다");
        }
        Long id = adminCategoryService.createCategory(request);
        return ResponseEntity.created(URI.create("/api/admin/category/sub/" + id)).build();
    }

    // ==================== 수정 API ====================

    @Operation(summary = "카테고리 수정", description = "기존 카테고리 정보를 수정합니다")
    @SwaggerSuccessResponse(status = 200, name = "카테고리 수정 성공")
    @PutMapping("/edit/{id}")
    public ResponseEntity<Void> updateCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "카테고리 수정 요청")
            @RequestBody UpdateCategoryRequest request) {
        adminCategoryService.updateCategory(id, request);
        return ResponseEntity.ok().build();
    }

    // ==================== 삭제 API ====================

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다")
    @SwaggerSuccessResponse(status = 204, name = "카테고리 삭제 성공")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long id) {
        adminCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}