package com.study.moya.posts.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts",
        indexes = {
                @Index(name = "idx_created_at", columnList = "createdAt"),
                @Index(name = "idx_title", columnList = "title"),
                @Index(name = "idx_is_deleted_views", columnList = "isDeleted, views")  // 추가된 복합 인덱스
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 모집 인원
    @Column
    private Integer recruits;

    //지원 현황
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    private List<Application> applications = new ArrayList<>();

    // 예상 기간
    @Column(length = 50)
    private String expectedPeriod;

    //대분류(스터디 분야)
    @ElementCollection
    @CollectionTable(name = "post_studies", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "study")
    private Set<String> studies = new HashSet<>();

    //중분류(스터디 과목)
    @ElementCollection
    @CollectionTable(name = "post_positions", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "study_details")
    private Set<String> studyDetails = new HashSet<>();

    // 게시글 상태
    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private Boolean isClosed = false;

    @Column
    private Integer views = 0;

    // 게시글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    // 댓글
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private Set<Comment> comments = new HashSet<>();

    // 스터디 시작일자
    @Column(nullable = false)
    private LocalDateTime startDate;

    // 게시글 종료일자 (선택적)
    private LocalDateTime endDate;

    // 마감일자 (선택적)
    private LocalDateTime closeDate;

    // 삭제일자 (선택적)
    private LocalDateTime deleteDate;

    @Builder
    public Post(String title, String content, Integer recruits,
                String expectedPeriod, Set<String> studies, Set<String> studyDetails, Member author,
                LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.content = content;
        this.recruits = recruits;
        this.expectedPeriod = expectedPeriod;
        this.studies = studies;
        this.studyDetails = studyDetails;
        this.author = author;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updatePost(String title, String content, Integer recruits,
                           String expectedPeriod, Set<String> studies, Set<String> studyDetails,
                           LocalDateTime startDate, LocalDateTime endDate) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (studies != null) {
            this.studies = studies;
        }
        if (recruits != null) {
            this.recruits = recruits;
        }
        if (expectedPeriod != null) {
            this.expectedPeriod = expectedPeriod;
        }
        if (studyDetails != null) {
            this.studyDetails = studyDetails;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    public void incrementViews() {
        this.views += 1;
    }

    //Soft Delete
    public void markAsDeleted() {
        this.isDeleted = true;
        this.deleteDate = LocalDateTime.now();
    }

}
