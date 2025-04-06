package com.example.cpsplatform.auth.generator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class UUIDAuthCodeGeneratorTest {
    @Test
    void generateAuthCode(){
        //given
        UUIDAuthCodeGenerator uuidAuthCodeGenerator = new UUIDAuthCodeGenerator();
        //when
        //then
        Assertions.assertThat(uuidAuthCodeGenerator.generateAuthCode().length()).isEqualTo(8);
    }

}