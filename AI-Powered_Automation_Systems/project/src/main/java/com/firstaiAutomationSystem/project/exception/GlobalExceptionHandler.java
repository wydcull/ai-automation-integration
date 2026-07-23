package com.firstaiAutomationSystem.project.exception;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Base handler for all AutomationException types
     */
    @ExceptionHandler(AutomationException.class)
    public ResponseEntity<Map<String, Object>> handleAutomationException(AutomationException ex) {
        log.error("Automation error [{}]: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    /**
     * Gmail rate limit - 429 Too Many Requests
     */
    @ExceptionHandler(GmailRateLimitException.class)
    public ResponseEntity<Map<String, Object>> handleGmailRateLimit(GmailRateLimitException ex) {
        log.warn("Gmail rate limit exceeded. Retry after {} seconds", ex.getRetryAfterSeconds());

        Map<String, Object> response = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                HttpStatus.TOO_MANY_REQUESTS.value()
        );
        response.put("retryAfter", ex.getRetryAfterSeconds());

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(response);
    }

    /**
     * Gmail quota exceeded - 403 Forbidden
     */
    @ExceptionHandler(GmailQuotaExceededException.class)
    public ResponseEntity<Map<String, Object>> handleGmailQuotaExceeded(GmailQuotaExceededException ex) {
        log.error("Gmail quota exceeded: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }

    /**
     * Gmail authentication errors - 401 Unauthorized
     */
    @ExceptionHandler(GmailAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleGmailAuth(GmailAuthenticationException ex) {
        log.error("Gmail authentication failed: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        "Gmail authentication failed. Please re-authorize the application.",
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    /**
     * AI service rate limits
     */
    @ExceptionHandler(AiServiceRateLimitException.class)
    public ResponseEntity<Map<String, Object>> handleAiRateLimit(AiServiceRateLimitException ex) {
        log.warn("AI service rate limit exceeded. Retry after {} seconds", ex.getRetryAfterSeconds());

        Map<String, Object> response = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                HttpStatus.TOO_MANY_REQUESTS.value()
        );
        response.put("retryAfter", ex.getRetryAfterSeconds());

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(response);
    }

    /**
     * AI service timeouts - 504 Gateway Timeout
     */
    @ExceptionHandler(AiServiceTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleAiTimeout(AiServiceTimeoutException ex) {
        log.error("AI service timeout: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        "AI service request timed out. Please try again.",
                        HttpStatus.GATEWAY_TIMEOUT.value()
                ));
    }

    /**
     * Network errors - 503 Service Unavailable
     */
    @ExceptionHandler({NetworkException.class, ServiceUnavailableException.class})
    public ResponseEntity<Map<String, Object>> handleNetworkError(AutomationException ex) {
        log.error("Network/Service error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        "Service temporarily unavailable. Please try again later.",
                        HttpStatus.SERVICE_UNAVAILABLE.value()
                ));
    }

    /**
     * Google API specific errors
     */
    @ExceptionHandler(GoogleJsonResponseException.class)
    public ResponseEntity<Map<String, Object>> handleGoogleApiError(GoogleJsonResponseException ex) {
        log.error("Google API error: Status={}, Message={}",
                ex.getStatusCode(), ex.getMessage());

        int statusCode = ex.getStatusCode();
        String errorCode = "GOOGLE_API_ERROR";
        String message = ex.getMessage();

        // Map specific Google error codes
        if (statusCode == 429) {
            errorCode = "GMAIL_RATE_LIMIT";
            message = "Gmail API rate limit exceeded. Please try again later.";
        } else if (statusCode == 403) {
            errorCode = "GMAIL_QUOTA_EXCEEDED";
            message = "Gmail API quota exceeded.";
        } else if (statusCode == 401) {
            errorCode = "GMAIL_AUTH_ERROR";
            message = "Gmail authentication failed.";
        }

        return ResponseEntity.status(statusCode)
                .body(createErrorResponse(errorCode, message, statusCode));
    }

    /**
     * Document processing errors
     */
    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentError(DocumentProcessingException ex) {
        log.error("Document processing error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        HttpStatus.UNPROCESSABLE_ENTITY.value()
                ));
    }

    /**
     * Generic exception fallback
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(
                        "INTERNAL_ERROR",
                        "An unexpected error occurred. Please contact support.",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    @ExceptionHandler(GmailApiException.class)
    public ResponseEntity<Map<String, Object>> handleGmailApiError(GmailApiException ex) {
        log.error("Gmail API error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        "Gmail API communication error. Please try again.",
                        HttpStatus.BAD_GATEWAY.value()
                ));
    }

    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<Map<String, Object>> handleAiServiceError(AiServiceException ex) {
        log.error("AI service error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        "AI service error occurred. Please try again.",
                        HttpStatus.BAD_GATEWAY.value()
                ));
    }

    @ExceptionHandler(AiParsingException.class)
    public ResponseEntity<Map<String, Object>> handleAiParsingError(AiParsingException ex) {
        log.error("AI parsing error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        "Failed to process AI response. Please try again.",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    @ExceptionHandler(UnsupportedDocumentTypeException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedDocument(UnsupportedDocumentTypeException ex) {
        log.warn("Unsupported document type: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(createErrorResponse(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()
                ));
    }

    /**
     * Helper method to create consistent error responses
     */
    private Map<String, Object> createErrorResponse(String errorCode, String message, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status);
        response.put("errorCode", errorCode);
        response.put("message", message);
        return response;
    }
}