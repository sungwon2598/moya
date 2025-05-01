package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "etc")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Etc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long etcType; // 1 또는 2

    @Builder
    public Etc(String name, Long etcType){
        this.name = name;
        this.etcType = etcType;
    }
}
