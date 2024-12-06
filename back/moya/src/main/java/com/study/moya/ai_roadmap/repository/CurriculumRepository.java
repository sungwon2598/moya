package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByCategory_Id(Long categoryId);
}
