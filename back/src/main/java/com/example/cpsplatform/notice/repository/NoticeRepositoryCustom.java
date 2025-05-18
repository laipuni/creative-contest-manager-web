package com.example.cpsplatform.notice.repository;

import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchResponse;
import com.example.cpsplatform.notice.repository.dto.AdminSearchNoticeCond;

public interface NoticeRepositoryCustom {
    public NoticeSearchResponse searchNoticeByAdminCond(final AdminSearchNoticeCond cond);
}
