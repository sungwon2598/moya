package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {
}