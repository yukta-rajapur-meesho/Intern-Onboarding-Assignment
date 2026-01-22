package com.example.smssender.Model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SmsRequest {

    @NotBlank(message = "Phone number must not be blank")
    @Pattern(
        regexp = "^\\+?[1-9][0-9]{7,14}$",
        message = "Phone number must be a valid international number"
    )

    private String phoneNumber;

    @NotBlank(message = "Message must not be blank")
    @Size(
        max = 160,
        message = "Message must not exceed 160 characters"
    )
    private String message;

    public SmsRequest(){

    }
    public SmsRequest(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

