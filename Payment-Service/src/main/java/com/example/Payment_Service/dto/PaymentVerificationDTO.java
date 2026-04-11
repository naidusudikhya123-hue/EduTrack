package com.example.Payment_Service.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentVerificationDTO {
    @NotBlank
    private String paymentId;

    @NotBlank
    private String code;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
