package com.example.cpsplatform.security.encoder;

import com.example.cpsplatform.exception.CryptoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.Callable;

@Slf4j
@Component
public class CryptoService {

    private final String secretKey;

    public CryptoService() throws IOException {
        ClassPathResource resource = new ClassPathResource("/AES_key.key");
        this.secretKey = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8).trim();
    }

    public String encryptAES(String data) {
        return handleException(() -> {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        });
    }

    public String decryptAES(String encodedData) {
        return handleException(() -> {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encodedData);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        });
    }

    public String sha256(String data) {
        return handleException(() -> {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encoded);
        });
    }

    public String handleException(Callable<String> callable){
        try {
            return callable.call();
        } catch (Exception e){
            throw new CryptoException(e);
        }
    }
}


