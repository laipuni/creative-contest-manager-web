package com.example.cpsplatform.notice.repository;

import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchResponse;
import com.example.cpsplatform.notice.controller.response.UserNoticeSearchResponse;
import com.example.cpsplatform.notice.repository.dto.AdminSearchNoticeCond;
import com.example.cpsplatform.notice.repository.dto.UserSearchNoticeCond;

public interface NoticeRepositoryCustom {
    public NoticeSearchResponse searchNoticeByAdminCond(final AdminSearchNoticeCond cond);

    public UserNoticeSearchResponse searchNoticeByUserCond(UserSearchNoticeCond cond);
}
