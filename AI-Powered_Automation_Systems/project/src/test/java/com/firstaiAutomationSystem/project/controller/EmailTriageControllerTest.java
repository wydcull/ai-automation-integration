package com.firstaiAutomationSystem.project.controller;

import com.firstaiAutomationSystem.project.service.EmailTriageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailTriageController.class)
class EmailTriageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailTriageService emailTriageService;

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        mockMvc.perform(multipart("/api/automation/email-triage")
                        .param("senderEmail", "invalid-email")
                        .param("subject", "Test Subject")
                        .param("body", "This is a test body"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankSenderEmail() throws Exception {
        mockMvc.perform(multipart("/api/automation/email-triage")
                        .param("senderEmail", "")
                        .param("subject", "Test")
                        .param("body", "Body content"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectShortBody() throws Exception {
        mockMvc.perform(multipart("/api/automation/email-triage")
                        .param("senderEmail", "test@example.com")
                        .param("subject", "Test")
                        .param("body", "Short"))
                .andExpect(status().isBadRequest());
    }
}
