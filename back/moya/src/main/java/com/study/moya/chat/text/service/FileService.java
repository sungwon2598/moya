package com.study.moya.chat.text.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final Path fileStorageLocation;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".png", ".jpg", ".jpeg", ".gif", ".mp4", ".mp3", ".wav", ".ogg");

    public FileService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다: " + ex.getMessage());
        }
    }

    public String storeFile(String roomId, MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // 파일 이름에 '..'이 포함되어 있는지 확인하여 경로 트래버설 공격 방지
            if(originalFileName.contains("..")) {
                throw new IOException("잘못된 파일 경로.");
            }

            // 파일 확장자 추출 및 검증
            String fileExtension = getFileExtension(originalFileName);
            if (!isAllowedExtension(fileExtension)) {
                throw new IOException("허용되지 않는 파일 형식입니다.");
            }

            // 고유 파일 이름 생성
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 방별 디렉토리 생성
            Path roomDir = this.fileStorageLocation.resolve(roomId);
            if (!Files.exists(roomDir)) {
                Files.createDirectories(roomDir);
            }

            // 파일 저장 경로
            Path targetLocation = roomDir.resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            //파일 경로에 대한 DB 저장 고민

            // 방별 파일 URL 생성
            String fileUrl = "/files/" + roomId + "/" + fileName;
            return fileUrl;

        } catch (IOException ex) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + ex.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(dotIndex).toLowerCase() : "";
    }

    private boolean isAllowedExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    // FileService에 파일 제공 로직 추가
    public Resource loadFileAsResource(String roomId, String fileName) throws MalformedURLException, FileNotFoundException {
        Path filePath = this.fileStorageLocation.resolve(roomId).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if(resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("파일을 찾을 수 없습니다: " + fileName);
        }
    }
}