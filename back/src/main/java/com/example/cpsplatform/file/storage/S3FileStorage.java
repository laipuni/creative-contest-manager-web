package com.example.cpsplatform.file.storage;


import com.amazonaws.services.s3.model.S3Object;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.s3.S3Service;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

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

    @Override
    public InputStream download(final String path, final String uploadName) {
        S3Object s3Object = s3Service.download(path, uploadName);
        return s3Object.getObjectContent();
    }
}
