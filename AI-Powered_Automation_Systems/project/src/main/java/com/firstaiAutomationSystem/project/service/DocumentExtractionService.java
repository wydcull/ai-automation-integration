package com.firstaiAutomationSystem.project.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentExtractionService {

    public String extractTextFromPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to extract text from PDF: " + file.getOriginalFilename(), e);
        }
    }
}