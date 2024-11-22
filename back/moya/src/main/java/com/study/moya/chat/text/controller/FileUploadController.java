package com.study.moya.chat.text.controller;

import com.study.moya.chat.text.dto.chat.FileChatDTO;
import com.study.moya.chat.text.dto.chat.MessageType;
import com.study.moya.chat.text.dto.chat.request.FileUploadRequest;
import com.study.moya.chat.text.dto.chat.request.FileUploadResponse;
import com.study.moya.chat.text.service.ChatService;
import com.study.moya.chat.text.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ws/chat/files")
public class FileUploadController {

    private final FileService fileService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/{roomId}/upload")
    public ResponseEntity<FileUploadResponse> uploadFile (
            @PathVariable String roomId,
            @ModelAttribute FileUploadRequest requestDTO) {
        try {
            String sender = requestDTO.sender();
            MultipartFile file = requestDTO.multipartFile();

            String fileUrl = fileService.storeFile(roomId, file);

            FileChatDTO fileMessage = new FileChatDTO(
                    MessageType.CHAT,//File 타입 추가
                    roomId,
                    sender,
                    "파일을 전송했습니다.",
                    fileUrl,
                    LocalDateTime.now()
            );//도착하는 시간기준으로 메세지 시간 설정 (고민하기)

            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, fileMessage);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new FileUploadResponse(fileUrl, "파일 업로드 성공"));
        } catch (Exception e) {
            //예외 구체화
            log.error("파일 업로드 실패 : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FileUploadResponse(null, "파일 업로드 실패"));
        }
    }
}
