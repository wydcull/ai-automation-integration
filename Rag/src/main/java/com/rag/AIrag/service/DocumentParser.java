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
            return sanitize(new String(file.getBytes(), StandardCharsets.UTF_8));
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
            return sanitize(text);
        }
    }

    /**
     * PostgreSQL rejects UTF-8 strings containing 0x00 (null bytes).
     * PDFBox often embeds these in extracted text from broken/embedded fonts.
     */
    private String sanitize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        // 1) Remove null bytes (the actual crash)
        String cleaned = text.replace("\u0000", "");
        // 2) Remove other control chars except \t \n \r
        cleaned = cleaned.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
        // 3) Normalize whitespace a bit (optional but helps chunking)
        cleaned = cleaned.replaceAll("[ \\t\\x0B\\f]+", " ");
        return cleaned.trim();
    }
}