package com.example.smssender.Kafka;

import com.example.smssender.Model.SmsEvent;

public interface SmsEventPublisher {
    void send(SmsEvent event);
}
