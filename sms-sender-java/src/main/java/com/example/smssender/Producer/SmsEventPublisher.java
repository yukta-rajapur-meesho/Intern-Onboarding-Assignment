package com.example.smssender.Producer;

import com.example.smssender.Model.SmsEvent;

public interface SmsEventPublisher {
    void send(SmsEvent event);
}
