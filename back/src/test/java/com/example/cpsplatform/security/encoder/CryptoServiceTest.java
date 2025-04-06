package com.example.cpsplatform.security.encoder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CryptoServiceTest {

    @Autowired
    CryptoService cryptoService;

    @DisplayName("데이터를 AES로 인코딩하고, 디코딩한다.")
    @Test
    void encryptAESAndDecryptAES() {
        //given
        String plainText = "대구광역시 북구 칠곡중앙대로136길 90";// 경북대 위치
        //when
        String encodedData = cryptoService.encryptAES(plainText);
        String decodedData = cryptoService.decryptAES(encodedData);
        //then
        assertThat(plainText).isEqualTo(decodedData);
    }

}