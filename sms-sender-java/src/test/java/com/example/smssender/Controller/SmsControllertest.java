package com.example.smssender.Controller;

import com.example.smssender.Model.SmsRequest;
import com.example.smssender.Service.SmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SmsController.class)
class SmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SmsService smsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn200_whenSmsIsSent() throws Exception {
        SmsRequest request = new SmsRequest(
                "1234567890",
                "Hello world");

        mockMvc.perform(post("/v1/sms/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("SMS processed"));

        verify(smsService).sendSms(argThat(req -> req.getPhoneNumber().equals("1234567890") &&
                req.getMessage().equals("Hello world")));

    }

    @Test
    void shouldReturn400_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/sms/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{jbewfubube}"))
                .andExpect(status().isBadRequest());
    }

}
