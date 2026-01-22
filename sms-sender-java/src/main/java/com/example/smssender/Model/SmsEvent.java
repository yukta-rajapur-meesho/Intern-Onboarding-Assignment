package com.example.smssender.Model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class SmsEvent {

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

    @NotNull(message = "Status must not be null")
    @Pattern(
        regexp = "SUCCESS|PROVIDER_ERROR",
        message = "Status must be one of: SUCCESS, PROVIDER_ERROR"
    )
    private String status;

    public SmsEvent() {}

    public SmsEvent(String phoneNumber, String message, String status) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
