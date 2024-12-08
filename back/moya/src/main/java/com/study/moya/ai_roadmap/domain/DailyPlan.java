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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_plan_id", nullable = false)
    private WeeklyPlan weeklyPlan;

    @OneToMany
    @JoinColumn(name = "daily_plan_id")
    private List<Quiz> quizzes = new ArrayList<>();

    @Builder
    private DailyPlan(Integer dayNumber, String keyword) {
        this.dayNumber = dayNumber;
        this.keyword = keyword;
    }

    public void setWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

}