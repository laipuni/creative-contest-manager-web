package com.example.cpsplatform.file.repository;


import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long> {

    //관리자 용, 기출 문제 전용 파일 조회 쿼리
    @Query("select f from File f where f.problem.id = :problemId AND f.fileType = :fileType AND f.deleted is FALSE")
    List<File> findAllByProblemIdAndFileTypeAndNotDeleted(@Param("problemId") Long problemId, @Param("fileType") FileType fileType);

}
