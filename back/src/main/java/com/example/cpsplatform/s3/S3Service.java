package com.example.cpsplatform.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.domain.FileExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws.s3.bucketName}")
    public String bucketName;

    private final AmazonS3 amazonS3;

    public void delete(final String fileName){
        amazonS3.deleteObject(bucketName,fileName);
    }

    public void upload(String path,final String uploadFileName, final byte[] fileBytes,
                        final String mimeType, final FileExtension extension, final long size) {
        File tempFile = writeFile(extension.getExtension(), fileBytes);
        putObject(path, uploadFileName, mimeType, size, tempFile, extension);
        closeFile(tempFile);
    }

    private void putObject(final String path, final String uploadFileName, final String mimeType, final long size, final File tempFile,final FileExtension extension) {
        try{
            amazonS3.putObject(new PutObjectRequest(bucketName + path, uploadFileName, tempFile)
                    .withMetadata(createObjectMetaData(size, mimeType)));
            log.info("s3에 파일 업로드, 파일이름= {}, 확장자 = {}",uploadFileName,extension.getExtension());
        } catch (AmazonS3Exception e){
            throw new IllegalStateException(e);
        }
    }

    private static File writeFile(final String extension, final byte[] fileBytes){
        File tempFile = getFile(extension);
        try(OutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(fileBytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return tempFile;
    }

    private static File getFile(final String extension) {
        try {
            return File.createTempFile("File","." + extension);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void closeFile(final File tempFile) {
        try(FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            fileOutputStream.close();
            if (tempFile.delete()) {
                log.debug("파일 업로드 후 fileOutputStream 닫기 성공했습니다.");
            } else {
                log.warn("파일 업로드 후 fileOutputStream 닫지 실패했습니다.");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ObjectMetadata createObjectMetaData(final long size, final String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }
}
