package com.example.cpsplatform.file.service.download.generator;

import java.util.List;
import java.util.Map;

public interface DownloadFileNameGenerator {

    public Map<Long,String> generate(List<Long> fileIds);

}
