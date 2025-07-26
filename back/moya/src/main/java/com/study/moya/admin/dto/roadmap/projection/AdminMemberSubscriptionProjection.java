package com.study.moya.admin.dto.roadmap.projection;

import java.time.LocalDateTime;

public interface AdminMemberSubscriptionProjection {
    Long getId();
    String getMainCategory();
    String getSubCategory();
    Integer getDuration();
}
