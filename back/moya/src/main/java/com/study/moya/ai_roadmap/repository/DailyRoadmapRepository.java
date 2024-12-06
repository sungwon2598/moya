package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.DailyRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyRoadmapRepository extends JpaRepository<DailyRoadmap, Long> {
    List<DailyRoadmap> findByWeeklyRoadmap_Id(Long weeklyRoadmapId);
}
