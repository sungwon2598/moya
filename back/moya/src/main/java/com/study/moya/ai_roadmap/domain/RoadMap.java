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

    @Column(name = "learning_objective")
    private String learningObjective;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne
    @JoinColumn(name = "etc1_id")
    private Etc etc1;

    @OneToOne
    @JoinColumn(name = "etc2_id")
    private Etc etc2;

    @ElementCollection
    @CollectionTable(name = "roadmap_tips", joinColumns = @JoinColumn(name = "roadmap_id"))
    @Column(name = "tip", columnDefinition = "TEXT")
    private List<String> overallTips = new ArrayList<>();

    @Builder
    private RoadMap(int goalLevel, String topic, int duration, String evaluation, List<String> overallTips,
                    Category category, String learningObjective, Etc etc1, Etc etc2) {
        this.duration = duration;
        this.goalLevel = goalLevel;
        this.topic = topic;
        this.evaluation = evaluation;
        this.category = category;
        this.learningObjective = learningObjective;
        if (overallTips != null) {
            this.overallTips = overallTips;
        }
        this.etc1 = etc1;
        this.etc2 = etc2;
    }

    // 기존 업데이트 메서드들
    public void updateTopic(String topic) {
        if (topic != null && topic.trim().isEmpty()) {
            throw new IllegalArgumentException("주제는 빈 값일 수 없습니다");
        }
        if (topic != null && topic.length() > 200) {
            throw new IllegalArgumentException("주제는 200자를 초과할 수 없습니다");
        }
        this.topic = topic != null ? topic.trim() : null;
    }

    public void updateEvaluation(String evaluation) {
        if (evaluation != null && evaluation.length() > 1000) {
            throw new IllegalArgumentException("평가는 1000자를 초과할 수 없습니다");
        }
        this.evaluation = evaluation != null ? evaluation.trim() : null;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateLearningObjective(String learningObjective) {
        if (learningObjective != null && learningObjective.length() > 500) {
            throw new IllegalArgumentException("학습 목표는 500자를 초과할 수 없습니다");
        }
        this.learningObjective = learningObjective != null ? learningObjective.trim() : null;
    }

    // ============ 새로 추가할 업데이트 메서드들 ============

    /**
     * 학습 기간 업데이트
     */
    public void updateDuration(Integer duration) {
        if (duration != null) {
            if (duration < 1 || duration > 52) {
                throw new IllegalArgumentException("학습 기간은 1주에서 52주 사이여야 합니다");
            }
            this.duration = duration;
        }
    }

    /**
     * 목표 레벨 업데이트
     */
    public void updateGoalLevel(Integer goalLevel) {
        if (goalLevel != null) {
            if (goalLevel < 1 || goalLevel > 5) {
                throw new IllegalArgumentException("목표 레벨은 1에서 5 사이여야 합니다");
            }
            this.goalLevel = goalLevel;
        }
    }

    /**
     * 전체 팁 업데이트
     */
    public void updateOverallTips(List<String> newTips) {
        if (newTips == null || newTips.isEmpty()) {
            throw new IllegalArgumentException("팁 목록은 비어있을 수 없습니다");
        }

        // 각 팁 검증
        for (String tip : newTips) {
            if (tip == null || tip.trim().isEmpty()) {
                throw new IllegalArgumentException("개별 팁은 빈 값일 수 없습니다");
            }
            if (tip.length() > 300) {
                throw new IllegalArgumentException("개별 팁은 300자를 초과할 수 없습니다");
            }
        }

        // 기존 팁 리스트 초기화 후 새로운 팁들 추가
        this.overallTips.clear();
        this.overallTips.addAll(newTips.stream()
                .map(String::trim)
                .filter(tip -> !tip.isEmpty())
                .distinct()
                .toList());
    }

    /**
     * 로드맵 기본 정보 일괄 업데이트
     */
    public void updateBasicInfo(String topic, Integer duration, String learningObjective, String evaluation) {
        if (topic != null) {
            updateTopic(topic);
        }
        if (duration != null) {
            updateDuration(duration);
        }
        if (learningObjective != null) {
            updateLearningObjective(learningObjective);
        }
        if (evaluation != null) {
            updateEvaluation(evaluation);
        }
    }

    /**
     * Etc 정보 업데이트
     */
    public void updateEtc1(Etc etc1) {
        this.etc1 = etc1;
    }

    public void updateEtc2(Etc etc2) {
        this.etc2 = etc2;
    }

    /**
     * 카테고리 및 Etc 정보 일괄 업데이트
     */
    public void updateCategoryInfo(Category category, Etc etc1, Etc etc2) {
        this.category = category;
        this.etc1 = etc1;
        this.etc2 = etc2;
    }
}