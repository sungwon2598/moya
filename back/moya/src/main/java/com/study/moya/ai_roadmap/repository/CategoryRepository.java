package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findAllMainCategories();

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId")
    List<Category> findSubCategories(@Param("parentId") Long parentId);

    boolean existsByNameAndParentId(String name, Long parentId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.parent.id = :categoryId")
    boolean hasSubCategories(@Param("categoryId") Long categoryId);

    @Query("SELECT c FROM Category c WHERE c.depth = :depth")
    List<Category> findByDepth(@Param("depth") int depth);
}