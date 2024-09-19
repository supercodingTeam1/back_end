package com.github.supercodingteam1.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException { //클라이언드에서 multiPart/form-data로 보낸 파일 가져와서
        File uploadFile = convert(multipartFile) //MultiparFile을 업로드하기 위한 File 객체로 변환
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName); //AWS S3 버킷에 업로드 하기 위한 메소드 진입
    }
    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName(); //fileName 설정
        String uploadImageUrl = putS3(uploadFile, fileName); //AWS S3에 업로드 하고 url 반환

        removeNewFile(uploadFile);  // convert()함수로 인해서 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile) //AWS 버킷에 fileName으로 uploadFile 객체를 업로드
                        .withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
        );
        return amazonS3.getUrl(bucket, fileName).toString(); //AWS 이미지 url return
    }

    private void removeNewFile(File targetFile) { //클라이언트가 보내온 파일을 로컬에서 업로드 이후 삭제하는 메소드
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws  IOException {
        String filename = UUID.randomUUID().toString().concat(Objects.requireNonNull(file.getOriginalFilename()));

        File convertFile = new File(filename); // 업로드한 파일의 이름
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
