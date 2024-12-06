package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByDailyRoadmap_Id(Long dailyRoadmapId);
}
