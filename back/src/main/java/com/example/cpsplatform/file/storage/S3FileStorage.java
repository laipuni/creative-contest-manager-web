package com.example.cpsplatform.file.storage;


import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.s3.S3Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3FileStorage implements FileStorage {

    private final S3Service s3Service;

    public S3FileStorage(final S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public void upload(final String path, final FileSources fileSources) {
        fileSources.getFileSourceList().forEach(fileSource -> {
            s3Service.upload(
                    path,
                    fileSource.getUploadFileName(),
                    fileSource.getFileBytes(),
                    fileSource.getMimeType(),
                    fileSource.getExtension(),
                    fileSource.getSize()
            );
        });
    }

    @Override
    public void delete(final String path, final String uploadFileName) {
        s3Service.delete(path + uploadFileName);
    }
}
