package com.study.moya.ai_roadmap.dto.response;

import com.study.moya.ai_roadmap.domain.Category;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryHierarchyResponse {
    private Long id;
    private String name;
    private List<CategoryHierarchyResponse> subCategories;

    public static CategoryHierarchyResponse from(Category category, List<CategoryHierarchyResponse> subCategories) {
        return CategoryHierarchyResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .subCategories(subCategories)
                .build();
    }
}