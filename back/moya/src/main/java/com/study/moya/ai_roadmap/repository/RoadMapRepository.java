package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.RoadMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {
}