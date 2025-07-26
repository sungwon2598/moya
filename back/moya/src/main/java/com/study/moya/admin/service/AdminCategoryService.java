package com.study.moya.admin.service;

import com.study.moya.ai_roadmap.domain.Category;
import com.study.moya.ai_roadmap.dto.request.CreateCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.UpdateCategoryRequest;
import com.study.moya.ai_roadmap.dto.response.CategoryHierarchyResponse;
import com.study.moya.ai_roadmap.dto.response.CategoryResponse;
import com.study.moya.ai_roadmap.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성 (어드민용)
     */
    public Long createCategory(CreateCategoryRequest request) {
        log.info("어드민 - 카테고리 생성 시작: {}", request.getName());

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리를 찾을 수 없습니다: " + request.getParentId()));

            if (parent.getDepth() >= Category.MAX_DEPTH - 1) {
                throw new IllegalArgumentException("최대 카테고리 깊이에 도달했습니다");
            }
        }

        if (categoryRepository.existsByNameAndParentId(request.getName(), request.getParentId())) {
            throw new IllegalArgumentException("해당 레벨에 동일한 이름의 카테고리가 이미 존재합니다");
        }

        Category category = Category.builder()
                .name(request.getName())
                .parent(parent)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("어드민 - 카테고리 생성 완료: ID {}, 이름 {}", savedCategory.getId(), savedCategory.getName());

        return savedCategory.getId();
    }

    /**
     * 카테고리 수정 (어드민용)
     */
    public void updateCategory(Long id, UpdateCategoryRequest request) {
        log.info("어드민 - 카테고리 수정 시작: ID {}, 새 이름 {}", id, request.getName());

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));

        String oldName = category.getName();
        category.updateName(request.getName());

        log.info("어드민 - 카테고리 수정 완료: ID {}, {} -> {}", id, oldName, request.getName());
    }

    /**
     * 카테고리 삭제 (어드민용)
     */
    public void deleteCategory(Long id) {
        log.info("어드민 - 카테고리 삭제 시작: ID {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));

        if (categoryRepository.hasSubCategories(id)) {
            throw new IllegalArgumentException("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다");
        }

        String categoryName = category.getName();
        categoryRepository.delete(category);

        log.info("어드민 - 카테고리 삭제 완료: ID {}, 이름 {}", id, categoryName);
    }

    /**
     * 전체 카테고리 계층 구조 조회 (어드민용)
     */
    @Transactional(readOnly = true)
    public List<CategoryHierarchyResponse> getCategoryHierarchy() {
        log.info("어드민 - 카테고리 계층 구조 조회");

        List<Category> mainCategories = categoryRepository.findAllMainCategories();
        List<CategoryHierarchyResponse> hierarchy = mainCategories.stream()
                .map(this::buildCategoryHierarchy)
                .collect(Collectors.toList());

        log.info("어드민 - 카테고리 계층 구조 조회 완료: 대분류 {}개", mainCategories.size());
        return hierarchy;
    }

    /**
     * 대분류 카테고리 조회 (어드민용)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getMainCategories() {
        log.info("어드민 - 대분류 카테고리 조회");

        List<CategoryResponse> mainCategories = categoryRepository.findAllMainCategories().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());

        log.info("어드민 - 대분류 카테고리 조회 완료: {}개", mainCategories.size());
        return mainCategories;
    }

    /**
     * 하위 카테고리 조회 (어드민용)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubCategories(Long parentId) {
        log.info("어드민 - 하위 카테고리 조회: 상위 ID {}", parentId);

        // 상위 카테고리 존재 확인
        categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("상위 카테고리를 찾을 수 없습니다: " + parentId));

        List<CategoryResponse> subCategories = categoryRepository.findSubCategories(parentId).stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());

        log.info("어드민 - 하위 카테고리 조회 완료: 상위 ID {}, 하위 {}개", parentId, subCategories.size());
        return subCategories;
    }

    /**
     * 깊이별 카테고리 조회 (어드민용)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByDepth(int depth) {
        log.info("어드민 - 깊이별 카테고리 조회: depth {}", depth);

        if (depth < 0) {
            throw new IllegalArgumentException("깊이는 0 이상이어야 합니다");
        }

        List<CategoryResponse> categories = categoryRepository.findByDepth(depth).stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());

        log.info("어드민 - 깊이별 카테고리 조회 완료: depth {}, {}개", depth, categories.size());
        return categories;
    }

    /**
     * 카테고리 계층 구조 빌드 (내부 메서드)
     */
    private CategoryHierarchyResponse buildCategoryHierarchy(Category category) {
        if (category.getDepth() >= 1) {
            return CategoryHierarchyResponse.from(category, List.of());
        }

        List<Category> subCategories = categoryRepository.findSubCategories(category.getId());
        List<CategoryHierarchyResponse> subCategoryResponses = subCategories.stream()
                .map(subCategory -> CategoryHierarchyResponse.from(subCategory, List.of()))
                .collect(Collectors.toList());

        return CategoryHierarchyResponse.from(category, subCategoryResponses);
    }
}