package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    public void updateKeyword(String newKeyword) {
        if (newKeyword == null || newKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("주차별 키워드는 빈 값일 수 없습니다");
        }
        if (newKeyword.length() > 150) {
            throw new IllegalArgumentException("주차별 키워드는 150자를 초과할 수 없습니다");
        }
        this.keyword = newKeyword.trim();
    }

    private void validateWeekNumber(Integer weekNumber) {
        if (weekNumber == null || weekNumber < 1) {
            throw new IllegalArgumentException("주차는 1 이상이어야 합니다.");
        }
    }
}
