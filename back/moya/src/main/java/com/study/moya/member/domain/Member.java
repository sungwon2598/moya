package com.study.moya.member.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.exception.MemberBlockedException;
import com.study.moya.member.exception.MemberWithdrawnException;
import com.study.moya.member.util.StringCryptoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "members",
        indexes = {
                @Index(name = "uk_nickname_status", columnList = "nickname, status", unique = true),
                @Index(name = "idx_created_at", columnList = "createdAt")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Value("${app.member.personal-info.retention-years}")
    private int personalInfoRetentionYears;

    @Value("${app.member.dormant.months}")
    private int dormantMonths;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 200)
    private String profileImageUrl;

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = false, updatable = false)
    private String providerId;

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MemberStatus status;

    @Version
    private Long version;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "personal_info_expiry_date")
    private LocalDateTime personalInfoExpiryDate;

    @Embedded
    private PrivacyConsent privacyConsent;

//    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private PointAccount pointAccount;

    @PrePersist
    public void prePersist() {
        this.lastLoginAt = LocalDateTime.now();
        this.personalInfoExpiryDate = calculateExpiryDate();
        //createPointAccount();
    }

    @Builder
    public Member(String email, String nickname, String providerId, String profileImageUrl,
                  Boolean termsAgreed, Boolean privacyPolicyAgreed, Boolean marketingAgreed) {
        this.email = email;
        this.nickname = nickname;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
        this.status = MemberStatus.ACTIVE;
        this.roles.add(Role.USER);
        this.lastLoginAt = LocalDateTime.now();
        this.personalInfoExpiryDate = calculateExpiryDate();
        this.privacyConsent = new PrivacyConsent(termsAgreed, privacyPolicyAgreed, marketingAgreed);
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusYears(personalInfoRetentionYears);
    }

//    public void createPointAccount() {
//        if (this.pointAccount == null) {
//            this.pointAccount = new PointAccount(this);
//        }
//    }

    public boolean shouldBeDormant() {
        return lastLoginAt.plusMonths(dormantMonths).isBefore(LocalDateTime.now());
    }

    public boolean shouldBeDeleted() {
        return LocalDateTime.now().isAfter(personalInfoExpiryDate);
    }

    public boolean isLoginable() {
        return this.status == MemberStatus.ACTIVE || this.status == MemberStatus.DORMANT;
    }

    public boolean isModifiable() {
        return this.status == MemberStatus.ACTIVE;
    }

    public void updateNickname(String nickname) {
        validateModifiable();
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        validateModifiable();
        this.profileImageUrl = profileImageUrl;
    }

    public void updateEmail(String email) {
        validateModifiable();
        this.email = email;
        extendPersonalInfoPeriod();
    }

    public void updateMarketingConsent(Boolean agreed) {
        validateModifiable();
        this.privacyConsent.updateMarketingConsent(agreed);
    }

    public void updateLoginInfo() throws MemberWithdrawnException, MemberBlockedException {
        validateLoginable();
        this.lastLoginAt = LocalDateTime.now();
        if (this.status == MemberStatus.DORMANT) {
            this.status = MemberStatus.ACTIVE;
        }
    }

    public void convertToDormant() {
        validateModifiable();
        this.status = MemberStatus.DORMANT;
    }

    public void block(String reason) {
        validateModifiable();
        this.status = MemberStatus.BLOCKED;
    }

    public void withdraw() {
        validateModifiable();
        this.status = MemberStatus.WITHDRAWN;
        clearPersonalInfo();
    }

    public void extendPersonalInfoPeriod() {
        this.personalInfoExpiryDate = calculateExpiryDate();
    }

    private void clearPersonalInfo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.email = "DELETED_" + timestamp;
        this.nickname = "DELETED_" + timestamp;
        this.profileImageUrl = null;
        this.providerId = "DELETED_" + timestamp;
        this.privacyConsent = null;
    }

    private void validateLoginable() throws MemberBlockedException, MemberWithdrawnException {
        if (this.status == MemberStatus.BLOCKED) {
            throw new MemberBlockedException("차단된 회원입니다.");
        }
        if (this.status == MemberStatus.WITHDRAWN) {
            throw new MemberWithdrawnException("탈퇴한 회원입니다.");
        }
    }

    private void validateModifiable() {
        if (!isModifiable()) {
            throw new IllegalStateException("수정할 수 없는 상태입니다. 현재 상태: " + this.status);
        }
    }
}