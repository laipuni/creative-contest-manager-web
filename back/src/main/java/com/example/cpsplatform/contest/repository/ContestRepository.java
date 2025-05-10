package com.example.cpsplatform.contest.repository;

import com.example.cpsplatform.contest.Contest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest,Long> {

    //관리자 전용, 대회 리스트 조회
    @Query("SELECT c FROM Contest c where c.deleted = false ORDER BY c.season ASC")
    Page<Contest> findContestList(Pageable pageable);

    //
    @Query("select c from Contest c where c.deleted = false order by c.season DESC limit 1")
    Optional<Contest> findLatestContest();

    //임시 삭제인 대회의 정보를 단건 조회 쿼리
    @Query(value = "SELECT * FROM Contest WHERE id = :id AND deleted = true", nativeQuery = true)
    Optional<Contest> findDeletedContestById(@Param("id") Long contestId);

    //임시 삭제인 대회의 정보를 리스트로 받아오는 쿼리
    @Query(value = "SELECT * FROM Contest WHERE deleted = true", nativeQuery = true)
    List<Contest> findDeletedContestById();

    @Modifying
    @Query(value = "delete from Contest where id = :contestId", nativeQuery = true)
    void hardDeleteById(@Param("contestId") Long contestId);
}

