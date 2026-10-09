package com.tilak.internship_platform.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class PdfServiceTest {

    @Test
    void extractsTextFromDocxResume() throws Exception {
        byte[] docxBytes;
        try (XWPFDocument document = new XWPFDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            document.createParagraph().createRun().setText("Java Spring Boot student resume");
            document.write(output);
            docxBytes = output.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                docxBytes);

        String extracted = new PdfService().extractText(file);

        assertTrue(extracted.contains("Java Spring Boot student resume"));
    }
}