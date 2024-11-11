package com.study.moya.auth.dto;

import com.study.moya.member.domain.Role;
import java.util.Set;

public record UserInfoResponse(String name, String email, Set<Role> roles) {
}