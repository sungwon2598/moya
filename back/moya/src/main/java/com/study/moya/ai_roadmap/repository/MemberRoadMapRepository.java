package com.study.moya.ai_roadmap.repository;

import com.study.moya.ai_roadmap.domain.MemberRoadMap;
import com.study.moya.ai_roadmap.domain.RoadMap;
import com.study.moya.ai_roadmap.dto.response.RoadMapSummaryDTO;
import com.study.moya.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRoadMapRepository extends JpaRepository<MemberRoadMap, Long> {
    // 특정 회원의 모든 로드맵 조회 (N+1 방지를 위한 페치 조인)
    @Query("SELECT mr FROM MemberRoadMap mr JOIN FETCH mr.roadMap WHERE mr.member.id = :memberId")
    List<MemberRoadMap> findByMemberIdWithRoadMap(@Param("memberId") Long memberId);

    @Query("SELECT new com.study.moya.ai_roadmap.dto.response.RoadMapSummaryDTO(" +
            "r.id, " +                // 1번째 파라미터 (id)
            "r.category.name, " +     // 2번째 파라미터 (mainCategory)
            "r.topic, " +             // 3번째 파라미터 (subCategory)
            "r.duration) " +          // 4번째 파라미터 (duration)
            "FROM MemberRoadMap mr JOIN mr.roadMap r " +
            "WHERE mr.member.id = :memberId")
    List<RoadMapSummaryDTO> findRoadMapSummariesByMemberId(@Param("memberId") Long memberId);

    boolean existsByMemberIdAndRoadMapId(Long memberId, Long roadMapId);

    Optional<MemberRoadMap> findByMemberIdAndRoadMapId(Long memberId, Long roadMapId);
}