package com.study.moya.member.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.constants.MemberConstants;
import com.study.moya.member.constants.MemberErrorCode;
import com.study.moya.member.exception.MemberException;
import com.study.moya.member.util.AESConverter;
import com.study.moya.member.util.StringCryptoConverter;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "members",
        indexes = {
                @Index(name = "uk_nickname_status", columnList = "nickname, status", unique = true),
                @Index(name = "idx_created_at", columnList = "createdAt")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = AESConverter.class)
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = true, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
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

    @Column(nullable = false)
    private String jwtRefreshToken;

    @Column(length = 500)
    private String introduction;



    @Embedded
    private PrivacyConsent privacyConsent;

    @Embedded
    private MemberOAuthToken memberOAuthToken;
//    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private PointAccount pointAccount;

    @PrePersist
    public void prePersist() {
        this.lastLoginAt = LocalDateTime.now();
        this.personalInfoExpiryDate = calculateExpiryDate();
        //createPointAccount();
    }

    @Builder
    private Member(String email, String password, String nickname, String providerId,
                   String profileImageUrl, Set<Role> roles, MemberStatus status, Long version,
                   Boolean termsAgreed, Boolean privacyPolicyAgreed, Boolean marketingAgreed,
                   String accessToken, String refreshToken, Instant tokenExpirationTime) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
        this.roles = roles != null ? roles : new HashSet<>();
        this.status = status;
        this.version = version;
        this.lastLoginAt = LocalDateTime.now();
        this.personalInfoExpiryDate = calculateExpiryDate();
        this.privacyConsent = PrivacyConsent.builder()
                .termsAgreed(termsAgreed)
                .privacyPolicyAgreed(privacyPolicyAgreed)
                .marketingAgreed(marketingAgreed)
                .build();
        this.memberOAuthToken = MemberOAuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenExpirationTime(tokenExpirationTime)
                .build();
    }

    public void updateOAuthTokens(String accessToken, String refreshToken, Instant tokenExpirationTime) {
        if (this.memberOAuthToken == null) {
            this.memberOAuthToken = MemberOAuthToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenExpirationTime(tokenExpirationTime)
                    .build();
        } else {
            this.memberOAuthToken.updateOAuthToken(accessToken, refreshToken, tokenExpirationTime);
        }
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusYears(MemberConstants.PERSONAL_INFO_RETENTION_YEARS);
    }

    public void updateJwtRefreshToken(String jwtRefreshToken) {
        this.jwtRefreshToken = jwtRefreshToken;
    }

//    public void createPointAccount() {
//        if (this.pointAccount == null) {
//            this.pointAccount = new PointAccount(this);
//        }
//    }

    public boolean shouldBeDormant() {
        return lastLoginAt.plusMonths(MemberConstants.DORMANT_MONTHS).isBefore(LocalDateTime.now());
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

    public void updatePassword(String newPassword) {
        validateModifiable();
        this.password = newPassword;
    }

    public void updateMarketingConsent(Boolean agreed) {
        validateModifiable();
        this.privacyConsent.updateMarketingConsent(agreed);
    }

    public void updateLoginInfo() {
        validateLoginable();
        this.lastLoginAt = LocalDateTime.now();
        if (this.status == MemberStatus.DORMANT) {
            this.status = MemberStatus.ACTIVE;
        }
    }

    public void updateIntroduction(String introduction) {
        validateModifiable();
        this.introduction = introduction;
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
        this.password = "DELETED_" + timestamp;
        this.nickname = "DELETED_" + timestamp;
        this.profileImageUrl = null;
        this.providerId = "DELETED_" + timestamp;
        this.privacyConsent = null;
    }

    private void validateLoginable() {
        if (this.status == MemberStatus.BLOCKED) {
            throw new MemberException(MemberErrorCode.MEMBER_BLOCKED);
        }
        if (this.status == MemberStatus.WITHDRAWN) {
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAWN);
        }
    }

    private void validateModifiable() {
        if (!isModifiable()) {
            throw new MemberException(
                    MemberErrorCode.MEMBER_NOT_MODIFIABLE,
                    MemberErrorCode.MEMBER_NOT_MODIFIABLE.getMessage(status.getStateMessage())
            );
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !shouldBeDeleted();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status != MemberStatus.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == MemberStatus.ACTIVE;
    }
}