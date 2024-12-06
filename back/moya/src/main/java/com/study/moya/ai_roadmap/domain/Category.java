package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;  // 카테고리명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;  // 상위 카테고리

//    @OneToMany(mappedBy = "parent")
//    private List<Category> children = new ArrayList<>(); //하위 카테고리(필요하면 주석 제거 후 사용)

    @Builder
    public Category(String categoryName, Category parent) {
        this.categoryName = categoryName;
        this.parent = parent;
    }
}
