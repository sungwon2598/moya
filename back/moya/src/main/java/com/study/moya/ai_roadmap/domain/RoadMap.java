package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private int goalLevel;

    private int duration;

    @Column(columnDefinition = "TEXT")
    private String evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ElementCollection
    @CollectionTable(name = "roadmap_tips", joinColumns = @JoinColumn(name = "roadmap_id"))
    @Column(name = "tip", columnDefinition = "TEXT")
    private List<String> overallTips = new ArrayList<>();

    @Builder
    private RoadMap(int goalLevel, String topic, int duration, String evaluation, List<String> overallTips, Category category) {
        this.duration = duration;
        this.goalLevel = goalLevel;
        this.topic = topic;
        this.evaluation = evaluation;
        this.category = category;
        if (overallTips != null) {
            this.overallTips = overallTips;
        }
    }

    public void updateTopic(String topic) {
        this.topic = topic;
    }

    public void updateEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}