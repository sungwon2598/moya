package com.study.moya.token.domain;

import com.study.moya.BaseEntity;
import com.study.moya.token.domain.enums.AiServiceType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "ai_services")
@Getter
public class AiService extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    private AiServiceType serviceType;

    private Long tokenCost;

    private Boolean isActive;

    // 생성자, 메서드 등
}