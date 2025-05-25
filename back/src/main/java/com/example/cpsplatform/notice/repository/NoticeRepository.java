package com.example.cpsplatform.notice.repository;

import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchResponse;
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.notice.repository.dto.AdminSearchNoticeCond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice,Long>, NoticeRepositoryCustom {

    @Modifying
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void increaseViewCount(@Param("id") Long id);
}
