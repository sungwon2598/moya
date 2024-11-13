package com.study.moya.member.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringCryptoConverter implements AttributeConverter<String, String> {
    private static final String KEY = "your-secret-key"; // 실제로는 외부 설정으로 관리
    private final AESEncryptor encryptor;

    public StringCryptoConverter() {
        this.encryptor = new AESEncryptor(KEY);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute != null ? encryptor.encrypt(attribute) : null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData != null ? encryptor.decrypt(dbData) : null;
    }
}