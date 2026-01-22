package com.example.smssender.Service;

import com.example.smssender.Model.SmsEvent;
import com.example.smssender.Model.SmsRequest;
import com.example.smssender.Producer.SmsEventPublisher;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private static final Logger logger = Logger.getLogger(SmsService.class.getName());

    private final SmsEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;

    @Value("${sms.provider.mock.success:true}")
    private boolean smsProviderMockSuccess;

    public SmsService(SmsEventPublisher eventPublisher, StringRedisTemplate redisTemplate) {
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
    }

    public void sendSms(SmsRequest request) {

        String phoneNumber = request.getPhoneNumber();

        if (isBlocked(phoneNumber)) {
            logger.warning(() -> String.format("SMS discarded: recipient %s is blocked", phoneNumber));
            return;
        }

        String status = invokeSmsProvider(request);

        try {
            SmsEvent event = new SmsEvent(request.getPhoneNumber(), request.getMessage(), status);
            eventPublisher.send(event);
        } catch (Exception e) {
            // Kafka failure should NOT break SMS sending
            logger.log(
                    Level.SEVERE,
                    String.format("Failed to publish SMS event to Kafka for %s", phoneNumber),
                    e);
        }

    }

    boolean isBlocked(String phoneNumber) {
        try {
            return redisTemplate.hasKey(phoneNumber);
        } catch (Exception e) {
            // Fail open: do not block SMS just because Redis is down
            logger.log(
                    Level.WARNING,
                    "Redis unreachable while checking blocklist. Defaulting to NOT blocked.",
                    e);
            return false;
        }
    }

    String invokeSmsProvider(SmsRequest request) {
        if (!smsProviderMockSuccess) {
            return "PROVIDER_ERROR";
        }
        return "SUCCESS";
    }

}