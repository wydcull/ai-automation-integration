package org.wydcull.ai_chatbot_project.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String SESSION_ID = "sessionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate unique trace ID for this request
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(TRACE_ID, traceId);

        // Log request details
        long startTime = System.currentTimeMillis();

        log.info(">>> Incoming Request: {} {} | IP: {} | TraceId: {}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                getClientIp(httpRequest),
                traceId);

        try {
            // Continue with request
            chain.doFilter(request, response);

            // Calculate response time
            long duration = System.currentTimeMillis() - startTime;

            // Log response details
            log.info("<<< Outgoing Response: {} {} | Status: {} | Duration: {}ms | TraceId: {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    duration,
                    traceId);

            // Log slow requests
            if (duration > 1000) {
                log.warn("SLOW REQUEST detected: {} took {}ms", httpRequest.getRequestURI(), duration);
            }

        } finally {
            // Clean up MDC
            MDC.remove(TRACE_ID);
            MDC.remove(SESSION_ID);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}