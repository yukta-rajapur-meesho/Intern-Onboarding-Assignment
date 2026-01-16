package com.example.smssender.Service;

import com.example.smssender.Kafka.SmsEventPublisher;
import com.example.smssender.Model.SmsEvent;
import com.example.smssender.Model.SmsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SmsServiceTest {

    @Mock
    private SmsEventPublisher eventPublisher;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private SmsService smsService;
    private SmsRequest request;

    @BeforeEach
    void setUp() {
        request = new SmsRequest("1234567890", "Hello world");
    }

    @Test
    void shouldNotSendSms_whenUserIsBlocked() {
        when(redisTemplate.hasKey("1234567890")).thenReturn(true);

        smsService.sendSms(request);

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldSendSmsAndPublishEvent_whenUserIsNotBlocked() {
        when(redisTemplate.hasKey("1234567890")).thenReturn(false);

        smsService.sendSms(request);

        verify(eventPublisher).send(any(SmsEvent.class));
    }

    @Test
    void shouldFailOpen_whenRedisIsUnavailable() {
        when(redisTemplate.hasKey(any()))
                .thenThrow(new RuntimeException("Redis down"));

        smsService.sendSms(request);

        verify(eventPublisher).send(any(SmsEvent.class));
    }

    @Test
    void shouldNotCrash_whenKafkaPublishFails() {
        when(redisTemplate.hasKey(any())).thenReturn(false);
        doThrow(new RuntimeException("Kafka down"))
                .when(eventPublisher).send(any(SmsEvent.class));

        smsService.sendSms(request);

        verify(eventPublisher).send(any(SmsEvent.class));
    }

    @Test
    void shouldPublishEvent_evenWhenProviderFails() {
        when(redisTemplate.hasKey(any())).thenReturn(false);

        // Force provider failure by spying
        SmsService spyService = Mockito.spy(smsService);
        doReturn("PROVIDER_ERROR")
                .when(spyService)
                .invokeSmsProvider(any(SmsRequest.class));

        spyService.sendSms(request);

        verify(eventPublisher).send(
                argThat(event -> event.getStatus().equals("PROVIDER_ERROR")));
    }

    // @Test
    // void sanityCheck() {
    //     fail("If you see this, tests are running");
    // }

}
