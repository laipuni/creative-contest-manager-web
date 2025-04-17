package com.example.cpsplatform.file.decoder.strategy;

import com.example.cpsplatform.file.domain.FileExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UUIDFileNamingStrategyTest {
    @Test
    void generate(){
        //given
        FileExtension extension = FileExtension.PDF;
        String originalFileName = "originalFileName";
        UUIDFileNamingStrategy uuidFileNamingStrategy = new UUIDFileNamingStrategy();
        //when
        //then
        assertThat(uuidFileNamingStrategy.generate(originalFileName,extension)).isNotBlank();
    }

}