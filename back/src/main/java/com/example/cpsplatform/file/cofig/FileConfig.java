package com.example.cpsplatform.file.cofig;

import com.example.cpsplatform.file.decoder.FileDecoder;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.file.storage.S3FileStorage;
import com.example.cpsplatform.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FileConfig {

    @Autowired
    S3Service s3Service;

    @Bean
    public FileStorage fileStorage(){
        return new S3FileStorage(s3Service);
    }

}
