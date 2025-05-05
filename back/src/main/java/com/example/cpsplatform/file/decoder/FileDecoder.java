package com.example.cpsplatform.file.decoder;

import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileDecoder<T> {
    public FileSources decode(List<T> file);

    public FileSource decode(T file);
}
