package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.Etc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtcRepository extends JpaRepository<Etc, Long> {
}
