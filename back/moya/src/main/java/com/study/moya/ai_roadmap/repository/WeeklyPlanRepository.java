package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.WeeklyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan, Long> {

    @Query("SELECT wp FROM WeeklyPlan wp " +
            "WHERE wp.roadMap.id = :roadmapId " +
            "AND wp.weekNumber = :weekNumber")
    Optional<WeeklyPlan> findByRoadmapIdAndWeek(
            @Param("roadmapId") Long roadmapId,
            @Param("weekNumber") Integer weekNumber
    );

    @Modifying
    @Query("DELETE FROM WeeklyPlan wp WHERE wp.roadMap.id = :roadmapId")
    void deleteByRoadMapId(@Param("roadmapId") Long roadmapId);
}
