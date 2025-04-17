package com.example.cpsplatform.file.storage;

import com.example.cpsplatform.file.decoder.vo.FileSources;

import java.util.List;

public interface FileStorage {

    public void upload(String path, FileSources fileSource);

}
