package com.study.moya.posts.dto.like;

public record LikeResponse(
        boolean liked,
        int totalLikes
) {}
