package com.ZigzagPayment.payment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBankAccountDto {
    @NotBlank
    private String name;
    @Email
    private String email;
}
