package com.milistock.develop.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Service
public class S3UploadService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    
    private final AmazonS3 amazonS3;

    // 올바른 파일 확장자인지 검증 (jpeg, png인지 검증)
    public void verfiedExtension(MultipartFile multipartFile) {
        
        String contentType = multipartFile.getContentType();

        // 이미지 존재하는지
        if (contentType==null) {
            throw new BusinessExceptionHandler("이미지를 첨부해야 합니다", ErrorCode.NULL_POINT_ERROR);
        }

        // 확장자가 jpeg, png인 파일들만 받아서 처리
        if (ObjectUtils.isEmpty(contentType) | (!contentType.contains("image/jpeg") & !contentType.contains("image/png")))            
            throw new BusinessExceptionHandler("jpeg 혹은 png가 아닙니다.", ErrorCode.INVALID_FILE_EXTENSION);
    }

    // EFFECT: Upload file to S3
    // RETURN: url of uploaded file
    public String upload(MultipartFile multipartFile) throws IOException {

        // 이미지 검증
        verfiedExtension(multipartFile);

        String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());

        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }
}
