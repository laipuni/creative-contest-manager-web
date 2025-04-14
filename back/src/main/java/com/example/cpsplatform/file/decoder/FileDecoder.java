package com.example.cpsplatform.file.decoder;

import com.example.cpsplatform.file.decoder.vo.FileSources;

public interface FileDecoder<T> {
    public FileSources decode(T file);

}
