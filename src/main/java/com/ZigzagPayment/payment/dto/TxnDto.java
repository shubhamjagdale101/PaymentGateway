package com.ZigzagPayment.payment.dto;

import com.ZigzagPayment.payment.constant.PaymentStatus;
import com.ZigzagPayment.payment.constant.TxnStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TxnDto {
    private String transactionId; // Unique identifier for the transaction
    private PaymentStatus status;
    private String paymentMethod;
}
