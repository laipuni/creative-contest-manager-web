package com.example.cpsplatform.file.cofig;

import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.service.download.generator.TeamSolveFileNameGenerator;
import com.example.cpsplatform.file.service.download.generator.DownloadFileNameGenerator;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.file.storage.S3FileStorage;
import com.example.cpsplatform.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    public static final String TEAM_SOLVE_ZIP_DOWNLOAD = "Team_solve";

    @Autowired
    S3Service s3Service;

    @Autowired
    FileRepository fileRepository;

    @Bean
    public FileStorage fileStorage(){
        return new S3FileStorage(s3Service);
    }

    @Bean(TEAM_SOLVE_ZIP_DOWNLOAD)
    public DownloadFileNameGenerator teamSolveFileNameGenerator(){
        return new TeamSolveFileNameGenerator(fileRepository);
    }

}
