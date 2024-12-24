package com.example.festimo.domain.post.service;

import com.example.festimo.exception.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    public String saveImage(MultipartFile image, String uploadDir) {
        try {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
            return "/uploads/" + filename;
        } catch (IOException e) {
            log.error("이미지를 저장하는 데 실패했습니다.", e);
            throw new ImageUploadException();
        }
    }
}