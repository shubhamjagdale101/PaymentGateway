package com.ZigzagPayment.payment.controller;

import com.ZigzagPayment.payment.dto.CreateBankAccountDto;
import com.ZigzagPayment.payment.dto.CreateCardDto;
import com.ZigzagPayment.payment.model.BankAccount;
import com.ZigzagPayment.payment.model.Card;
import com.ZigzagPayment.payment.response.ApiResponse;
import com.ZigzagPayment.payment.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ManagerController {
    @Autowired
    private ManagerService managerService;
    @PostMapping("/createBankAccount")
    public ApiResponse<Object> createBankAccount(@RequestBody @Valid CreateBankAccountDto createBankAccountDto){
        BankAccount account = managerService.createBankAccount(createBankAccountDto);
        return ApiResponse.success(account, "account created successfully", HttpStatus.OK.value());
    }

    @PostMapping("/createCard")
    public ApiResponse<Object> createCard(@RequestBody @Valid CreateCardDto createCardDto) throws Exception {
        Card card = managerService.createCard(createCardDto);
        return ApiResponse.success(card, "card created successfully", HttpStatus.OK.value());
    }
}
