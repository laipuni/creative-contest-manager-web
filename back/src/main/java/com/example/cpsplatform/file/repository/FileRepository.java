package com.example.cpsplatform.file.repository;


import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File,Long>, FileRepositoryCustom {

    //관리자 용, 기출 문제 전용 파일 조회 쿼리
    @Query("select f from File f where f.problem.id = :problemId AND f.fileType = :fileType AND f.deleted is FALSE")
    List<File> findAllByProblemIdAndFileTypeAndNotDeleted(@Param("problemId") Long problemId, @Param("fileType") FileType fileType);


    //관리자 용, 문제 파일 삭제 쿼리
    @Modifying
    @Query("update File f set f.deleted = true, f.problem = null where f.problem.id = :problemId")
    int softDeletedByProblemId(@Param("problemId") Long problemId);

    //답안지 제출 시 팀의 이전에 답안지 메다데이터 삭제 쿼리
    @Modifying
    @Query("update File f set f.deleted = true, f.teamSolve = null where f.teamSolve.id in :teamSolveIdList")
    int softDeletedByTeamSolveIdList(@Param("teamSolveIdList") List<Long> teamSolveIdList);

    //팀의 답안지 파일을 조회하는 쿼리
    Optional<File> findFileByTeamSolveId(Long teamSolveId);

    @Query("select exists (select f from File f inner join f.teamSolve ts where ts.team.id = :teamId and f.id = :fileId and f.deleted = false and f.fileType = :fileType)")
    boolean existsAnswerFileForTeam(@Param("teamId") Long teamId, @Param("fileId") Long fileId, @Param("fileType") FileType fileType);

    //팀들의 답안지 파일을 조회
    List<File> findAllByTeamSolve_IdIn(List<Long> teamSolveIds);

    @Modifying
    @Query(value = "delete from file where team_solve_id in :teamSolveIds",nativeQuery = true)
    int hardDeleteAllByTeamSolveIdIn(@Param("teamSolveIds")List<Long> teamSolveIds);

    @Modifying
    @Transactional
    @Query(value = "delete from file where id in :fileIds",nativeQuery = true)
    void hardDeleteAllByIdIn(@Param("fileIds")List<Long> fileIds);

    //팀들의 답안지 파일을 조회, 네이티브 쿼리 버전
    @Query(value = "select * from file where team_solve_id in (:teamSolveIds)"
            ,nativeQuery = true)
    List<File> findAllByTeamSolve_IdInNative(@Param("teamSolveIds") List<Long> teamSolveIds);

    //대회 문제 파일 조회, 네이티브 쿼리 버전
    @Query(value = "select * from file where problem_id in (:problemIds)"
            ,nativeQuery = true)
    List<File> findAllByProblem_IdInNative(@Param("problemIds")List<Long> problemIds);


    //공지사항의 id로 첨부파일들을 조회하는 쿼리
    List<File> findAllByNoticeId(Long noticeId);

    @Query(value = "select exists (select f from File f where f.notice.id = :noticeId and f.id = :fileId)")
    boolean existsFileByNoticeId(@Param("noticeId")Long noticeId, @Param("fileId") Long fileId);

}
