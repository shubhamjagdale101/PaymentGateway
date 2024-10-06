package com.ZigzagPayment.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitiatePaymentDto {
    @NotBlank
    private String receiverAccount;
    @NotNull
    private Integer totalAmount;
    @NotBlank
    private String callbackUrl;
}
