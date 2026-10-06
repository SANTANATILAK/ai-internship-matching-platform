package com.tilak.internship_platform.util;

import com.tilak.internship_platform.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private final Path uploadLocation;

    public FileStorageUtil(@Value("${app.upload.dir:uploads/resumes}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create resume upload directory: " + uploadDir, e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only PDF resumes are supported (.pdf format)");
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equalsIgnoreCase("application/pdf") && !contentType.contains("pdf")) {
            throw new BadRequestException("Invalid file format. Uploaded file is not a valid PDF.");
        }

        // Generate safe unique filename
        String safeFilename = UUID.randomUUID().toString() + ".pdf";

        try {
            Path targetLocation = this.uploadLocation.resolve(safeFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return safeFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store resume file: " + e.getMessage(), e);
        }
    }

    public Path getFilePath(String storedFilename) {
        return this.uploadLocation.resolve(storedFilename).normalize();
    }
}
