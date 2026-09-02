package com.rag.AIrag.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentParser {

    public String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Filename is missing");
        }

        String lower = filename.toLowerCase();

        if (lower.endsWith(".txt") || lower.endsWith(".md")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        if (lower.endsWith(".pdf")) {
            return extractPdfText(file.getBytes());
        }

        throw new IllegalArgumentException("Unsupported file type: " + filename);
    }

    private String extractPdfText(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);  // better reading order
            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException(
                        "No text found in PDF. It may be a scanned/image-only PDF.");
            }
            return text.trim();
        }
    }
}