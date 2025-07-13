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
@Table(name = "daily_plans")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DailyPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    private String keyword;

    @Column(columnDefinition = "TEXT")
    private String workSheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_plan_id")
    private WeeklyPlan weeklyPlan;

    @Builder
    private DailyPlan(Integer dayNumber, String keyword, String workSheet, WeeklyPlan weeklyPlan) {
        validateDayNumber(dayNumber);
        this.dayNumber = dayNumber;
        this.keyword = keyword;
        this.workSheet = workSheet;
        this.weeklyPlan = weeklyPlan;
    }

    public void updateWorkSheet(String workSheet) {
        this.workSheet = workSheet;
    }

    private void validateDayNumber(Integer dayNumber) {
        if (dayNumber == null || dayNumber < 1 || dayNumber > 7) {
            throw new IllegalArgumentException("일자는 1-7 사이여야 합니다.");
        }
    }

    @Override
    public String toString() {
        return "DailyPlan{" +
                "id=" + id +
                ", dayNumber=" + dayNumber +
                ", keyword='" + keyword + '\'' +
                ", workSheet='" + (workSheet != null ? workSheet.substring(0, Math.min(workSheet.length(), 100)) + "..." : null) + '\'' +
                ", weeklyPlanId=" + (weeklyPlan != null ? weeklyPlan.getId() : null) +
                '}';
    }
}