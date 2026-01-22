package com.example.smssender.Controller;

import com.example.smssender.Model.SmsRequest;
import com.example.smssender.Service.SmsService;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //every method in this class returns data
@RequestMapping("/v1/sms") //prefix route mapping
public class SmsController {

    private final SmsService smsService; //unique and assigned only once, cant be changed for the object instance

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@Valid @RequestBody SmsRequest request) { //look at http body and convert to this object
        smsService.sendSms(request);
        return ResponseEntity.ok("SMS processed"); //200 ok - success with data
    }
}
