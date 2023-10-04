package com.milistock.develop.controller.S3;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.milistock.develop.service.S3UploadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class FileUploadController {
    private final S3UploadService s3Upload;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("images") MultipartFile multipartFile) throws IOException {
        String uploadedUrl = s3Upload.upload(multipartFile);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(uploadedUrl);
    }
}
