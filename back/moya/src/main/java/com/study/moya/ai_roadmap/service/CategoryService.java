package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.domain.Category;
import com.study.moya.ai_roadmap.dto.request.BulkCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.BulkCategoryRequest.CategoryOperation;
import com.study.moya.ai_roadmap.dto.request.CreateCategoryRequest;
import com.study.moya.ai_roadmap.dto.request.UpdateCategoryRequest;
import com.study.moya.ai_roadmap.dto.response.CategoryHierarchyResponse;
import com.study.moya.ai_roadmap.dto.response.CategoryResponse;
import com.study.moya.ai_roadmap.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

            if (parent.getDepth() > 0) {
                throw new IllegalArgumentException("Cannot create category under sub-category");
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

        // 모든 ID를 한 번에 조회하여 캐싱
        List<Long> ids = request.getOperations().stream()
                .map(CategoryOperation::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        Map<Long, Category> categoryMap = categoryRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        for (int i = 0; i < request.getOperations().size(); i++) {
            CategoryOperation operation = request.getOperations().get(i);
            try {
                processOperation(operation, categoryMap);
            } catch (Exception e) {
                errors.add(String.format("Operation %d failed: %s", i, e.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Some operations failed: " + String.join(", ", errors));
        }
    }

    private void processOperation(CategoryOperation operation, Map<Long, Category> categoryMap) {
        switch (operation.getOperation()) {
            case CREATE -> createCategoryFromOperation(operation);
            case UPDATE -> updateCategoryFromOperation(operation, categoryMap);
            case DELETE -> deleteCategoryFromOperation(operation, categoryMap);
        }
    }

    private void createCategoryFromOperation(CategoryOperation operation) {
        Category parent = null;
        if (operation.getParentId() != null) {
            parent = categoryRepository.findById(operation.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));

            if (parent.getDepth() > 0) {
                throw new IllegalArgumentException("Cannot create category under sub-category");
            }
        }

        if (categoryRepository.existsByNameAndParentId(operation.getName(), operation.getParentId())) {
            throw new IllegalArgumentException("Category with this name already exists in this level");
        }

        Category category = Category.builder()
                .name(operation.getName())
                .parent(parent)
                .build();

        categoryRepository.save(category);
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
    }

    @Transactional(readOnly = true)
    public List<CategoryHierarchyResponse> getCategoryHierarchy() {
        List<Category> mainCategories = categoryRepository.findAllMainCategories();
        return mainCategories.stream()
                .map(category -> buildCategoryHierarchy(category))
                .collect(Collectors.toList());
    }

    private CategoryHierarchyResponse buildCategoryHierarchy(Category category) {
        List<Category> subCategories = categoryRepository.findSubCategories(category.getId());
        List<CategoryHierarchyResponse> subCategoryResponses = subCategories.stream()
                .map(subCategory -> CategoryHierarchyResponse.from(subCategory, List.of()))
                .collect(Collectors.toList());

        return CategoryHierarchyResponse.from(category, subCategoryResponses);
    }
}