package com.study.moya.admin.service;

import com.study.moya.admin.dto.member.AdminMemberDetailResponse;
import com.study.moya.admin.dto.member.AdminMemberResponse;
import com.study.moya.admin.dto.post.CommentSummary;
import com.study.moya.admin.dto.post.LikeSummary;
import com.study.moya.admin.dto.post.PostSummary;
import com.study.moya.admin.exception.AdminErrorCode;
import com.study.moya.admin.exception.AdminException;
import com.study.moya.error.constants.MemberErrorCode;
import com.study.moya.error.exception.MemberException;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.posts.domain.Comment;
import com.study.moya.posts.domain.Like;
import com.study.moya.posts.domain.Post;
import com.study.moya.posts.repository.CommentRepository;
import com.study.moya.posts.repository.LikeRepository;
import com.study.moya.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMemberService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public Page<AdminMemberResponse> getMembers(Pageable pageable) {
        // ID가 4 이상인 회원만 조회하도록 수정
//        Page<Member> membersPage = memberRepository.findByIdGreaterThanEqual(3701L, pageable);
        Page<Member> membersPage = memberRepository.findAll(pageable);
        return membersPage.map(this::convertToAdminMemberResponseDto);
    }

    public AdminMemberDetailResponse getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> AdminException.of(AdminErrorCode.MEMBER_NOT_FOUND));
        return convertToAdminMemberDetailResponseDto(member);
    }

    /**
     * 멤버 차단 처리 (관리자용)
     */
    @Transactional
    public void blockMember(Long memberId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw AdminException.of(AdminErrorCode.BLOCK_REASON_REQUIRED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> AdminException.of(AdminErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.BLOCKED) {
            throw AdminException.of(AdminErrorCode.ALREADY_BLOCKED_MEMBER);
        }

        member.block(reason);
    }

    /**
     * 멤버 탈퇴 처리 (관리자용)
     */
    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> AdminException.of(AdminErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw AdminException.of(AdminErrorCode.ALREADY_WITHDRAWN_MEMBER);
        }

        member.withdraw();
    }

    /**
     * 멤버 휴면 처리 (관리자용)
     */
    @Transactional
    public void dormantMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> AdminException.of(AdminErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DORMANT) {
            throw AdminException.of(AdminErrorCode.ALREADY_DORMANT_MEMBER);
        }

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw AdminException.of(AdminErrorCode.ALREADY_WITHDRAWN_MEMBER);
        }

        member.dormant();
    }

    /**
     * 멤버 차단 해제 처리 (관리자용)
     */
    @Transactional
    public void unblockMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> AdminException.of(AdminErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() != MemberStatus.BLOCKED) {
            throw AdminException.of(AdminErrorCode.NOT_BLOCKED_MEMBER);
        }

        member.unblock();  // Member 엔티티에 unblock() 메서드가 필요
    }

    /**
     * 상태별 멤버 조회
     */
    @Transactional(readOnly = true)
    public Page<AdminMemberResponse> getMembersByStatus(MemberStatus status, Pageable pageable) {
        Page<Member> membersPage = memberRepository.findByStatus(status, pageable);
        return membersPage.map(this::convertToAdminMemberResponseDto);
    }

    /**
     * 전체 상태별 멤버 개수 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getAllMemberCountsByStatus() {
        Map<String, Long> statusCounts = new HashMap<>();

        for (MemberStatus status : MemberStatus.values()) {
            long count = memberRepository.countByStatus(status);
            statusCounts.put(status.name(), count);
        }

        return statusCounts;
    }

    private AdminMemberResponse convertToAdminMemberResponseDto(Member member) {
        return AdminMemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .status(member.getStatus())
                .lastLoginAt(member.getLastLoginAt())
                .createdAt(member.getCreatedAt())
                .postCount(postRepository.countByAuthor(member))
                .commentCount(commentRepository.countByAuthor(member))
                .likeCount(likeRepository.countByMember(member))
                .build();
    }

    private AdminMemberDetailResponse convertToAdminMemberDetailResponseDto(Member member) {
        AdminMemberResponse memberResponse = convertToAdminMemberResponseDto(member);

        return AdminMemberDetailResponse.builder()
                .memberInfo(memberResponse)
                .posts(postRepository.findByAuthor(member).stream()
                        .map(PostSummary::new)
                        .collect(Collectors.toList()))
                .comments(commentRepository.findByAuthor(member).stream()
                        .map(CommentSummary::new)
                        .collect(Collectors.toList()))
                .likes(likeRepository.findByMember(member).stream()
                        .map(LikeSummary::new)
                        .collect(Collectors.toList()))
                .build();
    }
}