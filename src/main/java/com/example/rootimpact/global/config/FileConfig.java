package com.example.rootimpact.global.config;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            String absolutePath = System.getProperty("user.home") + "/uploads/";
            Files.createDirectories(Paths.get(absolutePath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory!");
        }
    }

    public String getUploadPath() {
        return System.getProperty("user.home") + "/uploads/";
    }
}
