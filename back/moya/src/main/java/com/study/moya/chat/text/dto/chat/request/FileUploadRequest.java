package com.study.moya.chat.text.dto.chat.request;

import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(
        String sender,
        MultipartFile multipartFile
) {
}
