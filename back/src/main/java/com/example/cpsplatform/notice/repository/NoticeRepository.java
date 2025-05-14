package com.example.cpsplatform.notice.repository;

import com.example.cpsplatform.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
}
