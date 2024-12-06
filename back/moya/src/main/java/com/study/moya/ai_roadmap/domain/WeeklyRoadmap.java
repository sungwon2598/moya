package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class WeeklyRoadmap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    private String weeklyKeyword;

    private int weekNumber;

    @Builder
    public WeeklyRoadmap(int weekNumber, Roadmap roadmap, String weeklyKeyword) {
        this.weekNumber = weekNumber;
        this.roadmap = roadmap;
        this.weeklyKeyword = weeklyKeyword;
    }
}
