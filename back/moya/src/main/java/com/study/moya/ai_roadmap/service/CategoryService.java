package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.domain.Category;
import com.study.moya.ai_roadmap.dto.request.BulkCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.BulkCategoryRequest.CategoryOperation;
import com.study.moya.ai_roadmap.dto.request.BulkCategoryRequest.OperationType;
import com.study.moya.ai_roadmap.dto.request.CreateCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.UpdateCategoryRequest;
import com.study.moya.ai_roadmap.dto.response.CategoryHierarchyResponse;
import com.study.moya.ai_roadmap.dto.response.CategoryResponse;
import com.study.moya.ai_roadmap.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long createCategory(CreateCategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));

            // MAX_DEPTH - 1 체크로 변경 (부모의 depth가 MAX_DEPTH-1 이하인 경우에만 생성 가능)
            if (parent.getDepth() >= Category.MAX_DEPTH - 1) {
                throw new IllegalArgumentException("Maximum category depth reached");
            }
        }

        if (categoryRepository.existsByNameAndParentId(request.getName(), request.getParentId())) {
            throw new IllegalArgumentException("Category with this name already exists in this level");
        }

        Category category = Category.builder()
                .name(request.getName())
                .parent(parent)
                .build();

        return categoryRepository.save(category).getId();
    }

    public List<CategoryResponse> getMainCategories() {
        return categoryRepository.findAllMainCategories().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getSubCategories(Long parentId) {
        return categoryRepository.findSubCategories(parentId).stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.updateName(request.getName());
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (categoryRepository.hasSubCategories(id)) {
            throw new IllegalArgumentException("Cannot delete category with sub-categories");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public void processBulkOperations(BulkCategoryRequest request) {
        List<String> errors = new ArrayList<>();

        // 모든 ID를 한 번에 조회하여 캐싱 (operation ID와 parentId 모두 포함)
        Set<Long> allIds = new HashSet<>();
        request.getOperations().forEach(op -> {
            if (op.getId() != null) allIds.add(op.getId());
            if (op.getParentId() != null) allIds.add(op.getParentId());
        });

        Map<Long, Category> categoryMap = categoryRepository.findAllById(allIds).stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        for (int i = 0; i < request.getOperations().size(); i++) {
            CategoryOperation operation = request.getOperations().get(i);
            try {
                validateOperation(operation, categoryMap);
                processOperation(operation, categoryMap);
            } catch (Exception e) {
                errors.add(String.format("Operation %d failed: %s", i, e.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Some operations failed: " + String.join(", ", errors));
        }
    }

    private void validateOperation(CategoryOperation operation, Map<Long, Category> categoryMap) {
        if (operation.getOperation() == OperationType.CREATE) {
            if (operation.getParentId() != null) {
                Category parent = categoryMap.get(operation.getParentId());
                if (parent == null) {
                    throw new IllegalArgumentException("Parent category not found");
                }

                // 부모의 depth를 확인하여 최대 depth 제한
                if (parent.getDepth() >= Category.MAX_DEPTH - 1) {
                    throw new IllegalArgumentException(
                            String.format("Cannot create category under depth %d", parent.getDepth()));
                }

                // targetDepth가 지정된 경우 추가 검증
                if (operation.getTargetDepth() != null) {
                    int expectedDepth = parent.getDepth() + 1;
                    if (operation.getTargetDepth() != expectedDepth) {
                        throw new IllegalArgumentException(
                                String.format("Invalid target depth. Expected: %d, Requested: %d",
                                        expectedDepth, operation.getTargetDepth()));
                    }
                }
            } else if (operation.getTargetDepth() != null && operation.getTargetDepth() != 0) {
                throw new IllegalArgumentException("Root category must have depth 0");
            }
        }
    }

    private void processOperation(CategoryOperation operation, Map<Long, Category> categoryMap) {
        switch (operation.getOperation()) {
            case CREATE -> createCategoryFromOperation(operation, categoryMap);
            case UPDATE -> updateCategoryFromOperation(operation, categoryMap);
            case DELETE -> deleteCategoryFromOperation(operation, categoryMap);
        }
    }

    private void createCategoryFromOperation(CategoryOperation operation, Map<Long, Category> categoryMap) {
        Category parent = null;
        if (operation.getParentId() != null) {
            parent = categoryMap.get(operation.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("Parent category not found");
            }
        }

        if (categoryRepository.existsByNameAndParentId(operation.getName(), operation.getParentId())) {
            throw new IllegalArgumentException("Category with this name already exists in this level");
        }

        Category category = Category.builder()
                .name(operation.getName())
                .parent(parent)
                .build();

        Category savedCategory = categoryRepository.save(category);
        // 새로 생성된 카테고리를 map에 추가
        categoryMap.put(savedCategory.getId(), savedCategory);
    }

    private void updateCategoryFromOperation(CategoryOperation operation, Map<Long, Category> categoryMap) {
        Category category = categoryMap.get(operation.getId());
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        category.updateName(operation.getName());
    }

    private void deleteCategoryFromOperation(CategoryOperation operation, Map<Long, Category> categoryMap) {
        Category category = categoryMap.get(operation.getId());
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        if (categoryRepository.hasSubCategories(category.getId())) {
            throw new IllegalArgumentException("Cannot delete category with sub-categories");
        }

        categoryRepository.delete(category);
        categoryMap.remove(category.getId());
    }

    @Transactional(readOnly = true)
    public List<CategoryHierarchyResponse> getCategoryHierarchy() {
        // 메인 카테고리(1단계) 조회
        List<Category> mainCategories = categoryRepository.findAllMainCategories();
        return mainCategories.stream()
                .map(this::buildCategoryHierarchy)
                .collect(Collectors.toList());
    }

    private CategoryHierarchyResponse buildCategoryHierarchy(Category category) {
        if (category.getDepth() >= 1) {
            // 이미 2단계 이상인 경우 더 이상 하위 카테고리를 조회하지 않음
            return CategoryHierarchyResponse.from(category, List.of());
        }

        // 직계 하위 카테고리만 조회 (2단계까지만)
        List<Category> subCategories = categoryRepository.findSubCategories(category.getId());
        List<CategoryHierarchyResponse> subCategoryResponses = subCategories.stream()
                .map(subCategory -> CategoryHierarchyResponse.from(subCategory, List.of()))
                .collect(Collectors.toList());

        return CategoryHierarchyResponse.from(category, subCategoryResponses);
    }

    public List<CategoryResponse> getSubSubCategories(Long parentId) {
        Category parent = validateCategoryExists(parentId);
        if (parent.getDepth() != 1) {
            throw new IllegalArgumentException("서브 카테고리의 하위 카테고리만 조회할 수 있습니다");
        }
        return categoryRepository.findSubCategories(parentId).stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
    private Category validateCategoryExists(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public List<CategoryResponse> getCategoriesByDepth(int depth) {
        return categoryRepository.findByDepth(depth).stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
}