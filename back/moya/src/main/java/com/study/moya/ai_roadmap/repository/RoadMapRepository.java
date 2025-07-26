package com.study.moya.ai_roadmap.repository;

import com.study.moya.admin.dto.roadmap.projection.AdminMemberSubscriptionProjection;
import com.study.moya.ai_roadmap.domain.RoadMap;
import com.study.moya.ai_roadmap.dto.response.RoadMapSimpleDto;
import java.util.List;

import com.study.moya.ai_roadmap.dto.response.RoadMapSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {
    @Query("SELECT new com.study.moya.ai_roadmap.dto.response.RoadMapSimpleDto(r.id, r.topic) " +
            "FROM RoadMap r " +
            "WHERE r.category.id = :categoryId")
    List<RoadMapSimpleDto> findRoadMapsByCategoryId(@Param("categoryId") Long categoryId);

    // RoadMapRepository에 추가
    @Query("SELECT r FROM RoadMap r " +
            "LEFT JOIN FETCH r.category " +
            "LEFT JOIN FETCH r.etc1 " +
            "LEFT JOIN FETCH r.etc2")
    List<RoadMap> findAllWithRelations();

    @Query(value = "SELECT " +
            "r.id as id, " +
            "CASE WHEN r.category_id IS NOT NULL THEN c.name ELSE e1.name END as mainCategory, " +
            "CASE WHEN r.category_id IS NOT NULL THEN r.topic ELSE e2.name END as subCategory, " +
            "r.duration as duration " +
            "FROM roadmaps r " +
            "LEFT JOIN categories c ON r.category_id = c.id " +
            "LEFT JOIN etc e1 ON r.etc1_id = e1.id " +
            "LEFT JOIN etc e2 ON r.etc2_id = e2.id " +
            "WHERE r.category_id = :categoryId " +
            "ORDER BY r.created_at DESC",
            nativeQuery = true)
    List<AdminMemberSubscriptionProjection> findAdminRoadMapsByCategoryId(@Param("categoryId") Long categoryId);
}