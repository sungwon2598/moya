package com.study.moya.admin.dto.member;

import com.study.moya.admin.dto.post.CommentSummary;
import com.study.moya.admin.dto.post.LikeSummary;
import com.study.moya.admin.dto.post.PostSummary;
import com.study.moya.member.domain.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminMemberDetailResponse {
    private AdminMemberResponse memberInfo;
    private List<PostSummary> posts;
    private List<CommentSummary> comments;
    private List<LikeSummary> likes;
}