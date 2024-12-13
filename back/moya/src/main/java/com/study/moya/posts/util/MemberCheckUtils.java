package com.study.moya.posts.util;

import com.study.moya.member.domain.Member;

public class MemberCheckUtils {

    public static Long getMemberId(Member member) {
        return (member != null) ? member.getId() : null;
    }

    public static boolean isAdmin(Member member) {
        return member != null && member.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}