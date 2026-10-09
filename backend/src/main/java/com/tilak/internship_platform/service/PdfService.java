package com.tilak.internship_platform.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Service
public class PdfService {

    public String extractText(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename() == null
                ? ""
                : file.getOriginalFilename().toLowerCase();

        if (filename.endsWith(".docx")) {
            try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(bytes));
                    XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                return extractor.getText();
            }
        }

        if (!filename.endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF and DOCX resumes are supported.");
        }

        try (var document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}