package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {

    public static final int MAX_DEPTH = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    private int depth;

    @Builder
    private Category(String name, Category parent) {
        if (parent != null && parent.getDepth() >= MAX_DEPTH) {
            throw new IllegalArgumentException("카테고리 최대 깊이 제한 도달");
        }
        this.name = name;
        this.parent = parent;
        this.depth = (parent != null) ? parent.getDepth() + 1 : 0;
    }

    public void updateName(String name) {
        this.name = name;
    }
}