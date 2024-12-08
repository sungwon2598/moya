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

    @OneToMany(mappedBy = "weeklyPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyPlan> dailyPlans = new ArrayList<>();

    @Builder
    private WeeklyPlan(Integer weekNumber, String keyword) {
        this.weekNumber = weekNumber;
        this.keyword = keyword;
    }

    public void addDailyPlan(DailyPlan dailyPlan) {
        this.dailyPlans.add(dailyPlan);
        dailyPlan.setWeeklyPlan(this); // 양방향 관계 설정
    }
}