package com.example.smssender.Producer;

import com.example.smssender.Model.SmsEvent;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SmsProducer implements SmsEventPublisher {

    private final KafkaTemplate<String, SmsEvent> kafkaTemplate;
    private static final Logger logger = Logger.getLogger(SmsProducer.class.getName());

    public SmsProducer(KafkaTemplate<String, SmsEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(SmsEvent event) {
        logger.log(Level.INFO, "Sending SMS event to Kafka: {}" + event.getMessage());
        kafkaTemplate.send("sms-messages", event.getPhoneNumber(), event);
    }
}