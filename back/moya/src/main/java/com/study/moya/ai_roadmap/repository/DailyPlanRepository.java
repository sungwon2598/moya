package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.DailyPlan;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    // 로드맵 ID로 모든 DailyPlan을 일자 순으로 조회
    @Query("SELECT d FROM DailyPlan d " +
            "JOIN FETCH d.weeklyPlan w " +
            "WHERE w.roadMap.id = :roadmapId " +
            "ORDER BY w.weekNumber, d.dayNumber")
    List<DailyPlan> findAllByWeeklyPlan_RoadMap_IdOrderByDayNumber(@Param("roadmapId") Long roadmapId);

    // 특정 주차의 모든 DailyPlan 조회
    @Query("SELECT d FROM DailyPlan d " +
            "WHERE d.weeklyPlan.id = :weeklyPlanId " +
            "ORDER BY d.dayNumber")
    List<DailyPlan> findAllByWeeklyPlanIdOrderByDayNumber(@Param("weeklyPlanId") Long weeklyPlanId);

    // 워크시트가 아직 생성되지 않은 DailyPlan 조회
    @Query("SELECT d FROM DailyPlan d " +
            "WHERE d.weeklyPlan.roadMap.id = :roadmapId " +
            "AND (d.workSheet IS NULL OR d.workSheet = '') " +
            "ORDER BY d.weeklyPlan.weekNumber, d.dayNumber")
    List<DailyPlan> findAllWithoutWorksheetByRoadmapId(@Param("roadmapId") Long roadmapId);

    // 특정 기간의 DailyPlan 조회
    @Query("SELECT d FROM DailyPlan d " +
            "WHERE d.weeklyPlan.roadMap.id = :roadmapId " +
            "AND d.weeklyPlan.weekNumber = :weekNumber " +
            "AND d.dayNumber BETWEEN :startDay AND :endDay " +
            "ORDER BY d.dayNumber")
    List<DailyPlan> findByRoadmapIdAndWeekNumberAndDayNumberBetween(
            @Param("roadmapId") Long roadmapId,
            @Param("weekNumber") Integer weekNumber,
            @Param("startDay") Integer startDay,
            @Param("endDay") Integer endDay
    );

    // 워크시트 업데이트가 필요한 DailyPlan 수 조회
    @Query("SELECT COUNT(d) FROM DailyPlan d " +
            "WHERE d.weeklyPlan.roadMap.id = :roadmapId " +
            "AND (d.workSheet IS NULL OR d.workSheet = '')")
    long countPendingWorksheets(@Param("roadmapId") Long roadmapId);

    // 특정 로드맵의 총 DailyPlan 수 조회
    @Query("SELECT COUNT(d) FROM DailyPlan d " +
            "WHERE d.weeklyPlan.roadMap.id = :roadmapId")
    long countTotalDailyPlans(@Param("roadmapId") Long roadmapId);

    //학습지만 조회
    @Query("SELECT d.workSheet FROM DailyPlan d " +
            "JOIN d.weeklyPlan w " +
            "WHERE w.roadMap.id = :roadmapId " +
            "AND d.workSheet IS NOT NULL AND d.workSheet != '' " +
            "ORDER BY w.weekNumber, d.dayNumber")
    List<String> findWorksheetsByRoadmapId(@Param("roadmapId") Long roadmapId);

    @Query("SELECT dp FROM DailyPlan dp " +
            "JOIN dp.weeklyPlan wp " +
            "WHERE wp.roadMap.id = :roadmapId " +
            "AND wp.weekNumber = :weekNumber " +
            "AND dp.dayNumber = :dayNumber")
    Optional<DailyPlan> findByRoadmapIdAndWeekAndDay(
            @Param("roadmapId") Long roadmapId,
            @Param("weekNumber") Integer weekNumber,
            @Param("dayNumber") Integer dayNumber
    );

    @Modifying
    @Query("DELETE FROM DailyPlan dp WHERE dp.weeklyPlan.roadMap.id = :roadmapId")
    void deleteByWeeklyPlanRoadMapId(@Param("roadmapId") Long roadmapId);
}