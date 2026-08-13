package com.firstaiAutomationSystem.project.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
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
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GmailAuthService {

    private static final Logger log = LoggerFactory.getLogger(GmailAuthService.class);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_SEND
    );
    private static final String USER_ID = "user";

    @Value("${gmail.credentials.file}")
    private String credentialsFilePath;

    @Value("${gmail.tokens.directory}")
    private String tokensDirectoryPath;

    @Value("${gmail.application.name}")
    private String applicationName;

    @Value("${gmail.redirect.uri}")
    private String redirectUri;

    private Gmail gmailService;
    private final Map<String, Long> oauthStates = new ConcurrentHashMap<>();

    public boolean isConnected() {
        try {
            Credential credential = loadStoredCredential();
            return credential != null && (
                    credential.getRefreshToken() != null
                            || (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() > 60)
            );
        } catch (Exception e) {
            log.debug("Gmail not connected: {}", e.getMessage());
            return false;
        }
    }

    public String createAuthorizationUrl() throws IOException, GeneralSecurityException {
        String state = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(new SecureRandom().generateSeed(24));
        oauthStates.put(state, System.currentTimeMillis());

        return buildFlow().newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(state)
                .set("prompt", "select_account consent")
                .build();
    }

    public boolean isValidState(String state) {
        Long created = oauthStates.remove(state);
        if (created == null) {
            return false;
        }
        return System.currentTimeMillis() - created < 10 * 60 * 1000;
    }

    public void handleCallback(String code) throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = buildFlow();
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();
        flow.createAndStoreCredential(tokenResponse, USER_ID);
        gmailService = null;
        log.info("Gmail OAuth callback stored credentials");
    }

    public Gmail getGmailService() throws IOException, GeneralSecurityException {
        if (!isConnected()) {
            throw new IOException("Gmail is not connected. Authorize from the UI first.");
        }
        if (gmailService == null) {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            gmailService = new Gmail.Builder(httpTransport, JSON_FACTORY, loadStoredCredential())
                    .setApplicationName(applicationName)
                    .build();
        }
        return gmailService;
    }

    public String getConnectedEmail() {
        try {
            return getGmailService().users().getProfile("me").execute().getEmailAddress();
        } catch (Exception e) {
            return null;
        }
    }

    public void invalidateToken() {
        gmailService = null;
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

    private Credential loadStoredCredential() throws IOException, GeneralSecurityException {
        return buildFlow().loadCredential(USER_ID);
    }

    private GoogleAuthorizationCodeFlow buildFlow() throws IOException, GeneralSecurityException {
        File credentialsFile = new File(credentialsFilePath);
        if (!credentialsFile.exists()) {
            throw new IOException("Credentials file not found: " + credentialsFilePath);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(new FileInputStream(credentialsFile))
        );
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build();
    }
}