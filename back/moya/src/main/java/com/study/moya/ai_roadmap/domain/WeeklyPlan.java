package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weekly_plans")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WeeklyPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false)
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private RoadMap roadMap;

    @Builder
    private WeeklyPlan(Integer weekNumber, String keyword, RoadMap roadMap) {
        validateWeekNumber(weekNumber);
        this.weekNumber = weekNumber;
        this.keyword = keyword;
        this.roadMap = roadMap;
    }

    private void validateWeekNumber(Integer weekNumber) {
        if (weekNumber == null || weekNumber < 1) {
            throw new IllegalArgumentException("주차는 1 이상이어야 합니다.");
        }
    }
}
