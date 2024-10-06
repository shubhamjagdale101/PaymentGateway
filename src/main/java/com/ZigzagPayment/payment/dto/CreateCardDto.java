package com.ZigzagPayment.payment.dto;

import com.ZigzagPayment.payment.constant.CardType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCardDto {
    private CardType cardType;
    @NotBlank
    private String bankAccountNumber;
}
