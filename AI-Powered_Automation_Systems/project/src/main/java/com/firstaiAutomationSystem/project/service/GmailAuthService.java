package com.firstaiAutomationSystem.project.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Service
public class GmailAuthService {

    private static final Logger log = LoggerFactory.getLogger(GmailAuthService.class);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_SEND
    );

    @Value("${gmail.credentials.file}")
    private String credentialsFilePath;

    @Value("${gmail.tokens.directory}")
    private String tokensDirectoryPath;

    @Value("${gmail.application.name}")
    private String applicationName;

    private Gmail gmailService;

    public Gmail getGmailService() throws IOException, GeneralSecurityException {
        if (gmailService == null) {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            gmailService = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                    .setApplicationName(applicationName)
                    .build();
        }
        return gmailService;
    }

    private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets
        log.info("Loading Gmail credentials from: {}", credentialsFilePath);

        File credentialsFile = new File(credentialsFilePath);
        if (!credentialsFile.exists()) {
            log.error("Gmail credentials file not found: {}", credentialsFilePath);
            throw new IOException("Credentials file not found: " + credentialsFilePath);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(new FileInputStream(credentialsFile))
        );

        // Build flow and trigger user authorization request
        log.info("Gmail OAuth authorization started");
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(-1).build();
        log.info("Gmail OAuth authorization completed successfully");
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void invalidateToken() {
        gmailService = null;
        // Optionally delete token files
        File tokensDir = new File(tokensDirectoryPath);
        if (tokensDir.exists() && tokensDir.isDirectory()) {
            File[] files = tokensDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}
