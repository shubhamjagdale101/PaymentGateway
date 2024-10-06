package com.ZigzagPayment.payment.dto;

import com.ZigzagPayment.payment.constant.CardType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentDto {
    @NotBlank(message = "cardNumber should not blank")
    private String cardNumber;
    @NotBlank(message = "Otp should not blank")
    private String otp;
    @NotBlank(message = "TxnId should not blank")
    private String txnId;
    @NotBlank(message = "Expiry Date should not blank")
    private String expiryDate;
    @NotBlank(message = "cvv number should not blank")
    private String cvv;
    private CardType paymentType;
}
