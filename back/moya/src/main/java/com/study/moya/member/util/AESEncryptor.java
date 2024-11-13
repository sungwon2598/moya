package com.study.moya.member.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AESEncryptor {
    private final String key;
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    public String encrypt(String value) {
        try {
            SecretKey secretKey = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = generateIv();

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
            byte[] encryptedData = cipher.doFinal(value.getBytes());

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            SecretKey secretKey = generateKey();
            byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);

            // IV 추출
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedValue);
            byte[] iv = new byte[12];
            byteBuffer.get(iv);

            // 암호화된 데이터 추출
            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            return new String(cipher.doFinal(encryptedData));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    private SecretKey generateKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(hash, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Key generation failed", e);
        }
    }

    private byte[] generateIv() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}