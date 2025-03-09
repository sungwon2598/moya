package com.study.moya.admin.dto.member;

import com.study.moya.member.domain.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminMemberResponse {
    private Long id;
    private String email;
    private String nickname;
    private MemberStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private int postCount;
    private int commentCount;
    private int likeCount;
}

