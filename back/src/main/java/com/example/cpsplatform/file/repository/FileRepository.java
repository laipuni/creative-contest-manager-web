package com.example.cpsplatform.file.repository;


import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long>, FileRepositoryCustom {

    //관리자 용, 기출 문제 전용 파일 조회 쿼리
    @Query("select f from File f where f.problem.id = :problemId AND f.fileType = :fileType AND f.deleted is FALSE")
    List<File> findAllByProblemIdAndFileTypeAndNotDeleted(@Param("problemId") Long problemId, @Param("fileType") FileType fileType);


    //관리자 용, 문제 파일 삭제 쿼리
    @Modifying
    @Query("update File f set f.deleted = true, f.problem = null where f.problem.id = :problemId")
    int softDeletedByProblemId(@Param("problemId") Long problemId);
}
