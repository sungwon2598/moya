package com.study.moya.token.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "token_packages")
@Getter
public class TokenPackage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packageName;

    private Long tokenAmount;

    private Long price;

    @Column(length = 3)
    private String currency;

    private Boolean isActive;

    // 생성자, 메서드 등
}