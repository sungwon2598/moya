package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import com.study.moya.ai_roadmap.constants.LearningObjective;
import jakarta.persistence.*;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_objective")
    private LearningObjective learningObjective;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ElementCollection
    @CollectionTable(name = "roadmap_tips", joinColumns = @JoinColumn(name = "roadmap_id"))
    @Column(name = "tip", columnDefinition = "TEXT")
    private List<String> overallTips = new ArrayList<>();

    @Builder
    private RoadMap(int goalLevel, String topic, int duration, String evaluation, List<String> overallTips,
                    Category category, LearningObjective learningObjective) {
        this.duration = duration;
        this.goalLevel = goalLevel;
        this.topic = topic;
        this.evaluation = evaluation;
        this.category = category;
        this.learningObjective = learningObjective;
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

    public void updateLearningObjective(LearningObjective learningObjective) {
        this.learningObjective = learningObjective;
    }
}