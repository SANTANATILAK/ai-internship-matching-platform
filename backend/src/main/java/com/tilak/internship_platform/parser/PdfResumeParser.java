package com.tilak.internship_platform.parser;

import com.tilak.internship_platform.exception.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PdfResumeParser {

    private static final Logger logger = LoggerFactory.getLogger(PdfResumeParser.class);

    public String extractText(File pdfFile) {
        if (!pdfFile.exists() || !pdfFile.canRead()) {
            throw new BadRequestException("PDF file cannot be read or does not exist");
        }

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            if (document.isEncrypted()) {
                throw new BadRequestException("Uploaded PDF is password-protected or encrypted. Please upload an unlocked PDF.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new BadRequestException("No readable text found in the PDF. Scanned images/photos of resumes are not supported.");
            }

            return text;
        } catch (IOException e) {
            logger.error("Failed to parse PDF document: {}", e.getMessage());
            throw new BadRequestException("Failed to parse PDF document: " + e.getMessage());
        }
    }
}
