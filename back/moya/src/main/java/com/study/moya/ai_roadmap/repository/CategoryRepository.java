package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
    List<Category> findByParentIsNull();  // 최상위 카테고리 조회
}
