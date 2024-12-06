package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.WeeklyRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeeklyRoadmapRepository extends JpaRepository<WeeklyRoadmap, Long> {
    List<WeeklyRoadmap> findByRoadmap_Id(Long roadmapId);
}
