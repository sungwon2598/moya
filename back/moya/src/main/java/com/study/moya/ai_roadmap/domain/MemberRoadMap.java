package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_roadmaps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberRoadMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private RoadMap roadMap;

    @Builder
    public MemberRoadMap(Member member, RoadMap roadMap) {
        this.member = member;
        this.roadMap = roadMap;
    }
}