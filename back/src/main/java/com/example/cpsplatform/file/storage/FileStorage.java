package com.example.cpsplatform.file.storage;

import com.example.cpsplatform.file.decoder.vo.FileSources;

import java.io.InputStream;
import java.util.List;

public interface FileStorage {

    public void upload(final String path, FileSources fileSource);
    public void delete(final String path, final String uploadFileName);

    public InputStream download(final String path, final String uploadName);
}
