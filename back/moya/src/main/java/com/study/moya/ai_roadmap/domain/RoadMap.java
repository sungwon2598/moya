package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roadmaps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoadMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String evaluation;

    @ElementCollection
    @CollectionTable(name = "roadmap_tips", joinColumns = @JoinColumn(name = "roadmap_id"))
    @Column(name = "tip", columnDefinition = "TEXT")
    private List<String> overallTips = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "curriculum_id")
    private List<WeeklyPlan> weeklyPlans = new ArrayList<>();

    @Builder
    private RoadMap(String topic, String evaluation, List<String> overallTips) {
        this.topic = topic;
        this.evaluation = evaluation;
        if (overallTips != null) {
            this.overallTips = overallTips;
        }
    }

    public void addWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlans.add(weeklyPlan);
    }
}