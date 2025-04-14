package com.example.cpsplatform.file.repository;


import com.example.cpsplatform.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {
}
