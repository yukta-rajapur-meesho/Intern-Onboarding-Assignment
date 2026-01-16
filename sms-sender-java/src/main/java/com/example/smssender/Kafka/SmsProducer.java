package com.example.smssender.Kafka;

import com.example.smssender.Model.SmsEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SmsProducer implements SmsEventPublisher {

    private final KafkaTemplate<String, SmsEvent> kafkaTemplate;

    public SmsProducer(KafkaTemplate<String, SmsEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(SmsEvent event) {
        kafkaTemplate.send("sms-events", event.getPhoneNumber(), event);
    }
}