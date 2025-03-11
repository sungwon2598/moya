package com.study.moya.ai_roadmap.controller;


import com.study.moya.ai_roadmap.constants.LearningObjective;
import com.study.moya.ai_roadmap.domain.Category;
import com.study.moya.ai_roadmap.dto.request.BulkCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.CreateCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.CreateSubCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.UpdateCategoryRequest;
import com.study.moya.ai_roadmap.dto.response.CategoryHierarchyResponse;
import com.study.moya.ai_roadmap.dto.response.CategoryResponse;
import com.study.moya.ai_roadmap.service.CategoryService;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody CreateCategoryRequest request) {
        Long id = categoryService.createCategory(request);
        return ResponseEntity.created(URI.create("/api/categories/" + id)).build();
    }

    @GetMapping("/main") // 대분류 카테고리 전체 조회
    public ResponseEntity<List<CategoryResponse>> getMainCategories() {
        return ResponseEntity.ok(categoryService.getMainCategories());
    }

    @GetMapping("/{parentId}/sub")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubCategories(parentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable Long id,
            @RequestBody UpdateCategoryRequest request
    ) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<Void> processBulkOperations(@RequestBody BulkCategoryRequest request) {
        categoryService.processBulkOperations(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hierarchy") // 대분류, 중분류 전체
    public ResponseEntity<List<CategoryHierarchyResponse>> getCategoryHierarchy() {
        return ResponseEntity.ok(categoryService.getCategoryHierarchy());
    }

    @GetMapping("/roadmap-form-data")
    public ResponseEntity<Map<String, Object>> getRoadmapFormData() {
        Map<String, Object> formData = new HashMap<>();

        // 카테고리 계층 구조 가져오기
        List<CategoryHierarchyResponse> categories = categoryService.getCategoryHierarchy();

        // 학습 목표 목록 가져오기
        List<Map<String, String>> objectives = Arrays.stream(LearningObjective.values())
                .map(objective -> Map.of(
                        "code", objective.name(),
                        "description", objective.getDescription()
                ))
                .collect(Collectors.toList());

        formData.put("categories", categories);
        formData.put("learningObjectives", objectives);

        return ResponseEntity.ok(formData);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @GetMapping("/{parentId}/sub-sub") //최하위 카테고리 조회
    public ResponseEntity<List<CategoryResponse>> getSubSubCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubSubCategories(parentId));
    }

    // 서브 카테고리(2단계) 생성
    @PostMapping("/sub")
    public ResponseEntity<Void> createSubCategory(@RequestBody CreateSubCategoryRequest request) {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest(request.getName(), request.getParentId());
        Long id = categoryService.createCategory(categoryRequest);
        return ResponseEntity.created(URI.create("/api/categories/" + id)).build();
    }

    // 서브-서브 카테고리(3단계) 생성
    @PostMapping("/sub/sub")
    public ResponseEntity<Void> createSubSubCategory(@RequestBody CreateSubCategoryRequest request) {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest(request.getName(), request.getParentId());
        Long id = categoryService.createCategory(categoryRequest);
        return ResponseEntity.created(URI.create("/api/categories/" + id)).build();
    }

    // 특정 depth의 카테고리 조회
    @GetMapping("/depth/{depth}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByDepth(@PathVariable int depth) {
        if (depth < 0 || depth >= Category.MAX_DEPTH) {
            throw new IllegalArgumentException("Invalid depth");
        }
        List<CategoryResponse> categories = categoryService.getCategoriesByDepth(depth);
        return ResponseEntity.ok(categories);
    }
}