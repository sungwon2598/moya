package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.RoadMap;
import com.study.moya.ai_roadmap.dto.response.RoadMapSimpleDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {
    @Query("SELECT new com.study.moya.ai_roadmap.dto.response.RoadMapSimpleDto(r.id, r.topic) " +
            "FROM RoadMap r " +
            "WHERE r.category.id = :categoryId")
    List<RoadMapSimpleDto> findRoadMapsByCategoryId(@Param("categoryId") Long categoryId);
}