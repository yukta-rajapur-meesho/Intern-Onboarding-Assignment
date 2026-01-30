package com.example.smssender.Service;

import com.example.smssender.Model.SmsEvent;
import com.example.smssender.Model.SmsRequest;
import com.example.smssender.Producer.SmsEventPublisher;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private static final Logger logger = Logger.getLogger(SmsService.class.getName());

    private final SmsEventPublisher eventPublisher;
    private final BlockListService blockListService;

    @Value("${sms.provider.mock.success:true}")
    private boolean smsProviderMockSuccess;

    public SmsService(SmsEventPublisher eventPublisher, BlockListService blockListService) {
        this.eventPublisher = eventPublisher;
        this.blockListService = blockListService;
    }

    public String sendSms(SmsRequest request) {

        String phoneNumber = request.getPhoneNumber();

        if (isBlocked(phoneNumber)) {
            logger.warning(() -> String.format("SMS discarded: recipient %s is blocked", phoneNumber));
            return "This number is blocked, cannot send SMS";
        }

        String status = invokeSmsProvider(request);

        if ("PROVIDER_ERROR".equals(status)) {
            return "Failed: Due to provider error";
        }

        try {
            SmsEvent event = new SmsEvent(request.getPhoneNumber(), request.getMessage(), status);
            eventPublisher.send(event);
        } catch (Exception e) {
            // Kafka failure should NOT break SMS sending
            logger.log(
                    Level.SEVERE,
                    String.format("Failed to publish SMS event to Kafka for %s", phoneNumber),
                    e);
            return "Failed: Internal server error,cannot send SMS";
        }
        return "SMS sent successfully";

    }

    boolean isBlocked(String phoneNumber) {
        try {
            return blockListService.isBlocked(phoneNumber);
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