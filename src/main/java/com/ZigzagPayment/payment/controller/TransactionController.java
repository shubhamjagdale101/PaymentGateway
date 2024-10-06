package com.ZigzagPayment.payment.controller;

import com.ZigzagPayment.payment.dto.InitiatePaymentDto;
import com.ZigzagPayment.payment.dto.PaymentDto;
import com.ZigzagPayment.payment.response.ApiResponse;
import com.ZigzagPayment.payment.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:4200"}, allowCredentials = "true")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/initiatePayment")
    @ResponseBody
    public ApiResponse<Object> initiatePayment(@RequestBody @Valid InitiatePaymentDto payment) throws Exception {
        return transactionService.processInitiatePayment(payment);
    }

    @GetMapping("/payment/{txnId}")
    public String paymentInterfaceProvider(Model model, @PathVariable("txnId") String txnId){
        return transactionService.handlePaymentInterface(model, txnId);
    }

    @GetMapping("/sendOtp/{senderCardNumber}")
    @ResponseBody
    public ApiResponse<String> sendOtpToEmail(@PathVariable("senderCardNumber") String senderCardNumber) throws Exception {
        log.info("into controller");
        String res = transactionService.sendOtpToSenderEmail(senderCardNumber);
        return ApiResponse.success("", "", 200);
    }

    @PostMapping("/makePayment")
    @ResponseBody
    public ApiResponse<Object> makePayment(@RequestBody @Valid PaymentDto paymentDto) throws Exception {
        return transactionService.makePayment(paymentDto);
    }

    @GetMapping("/redirect/{txnId}")
    public RedirectView redirect(@PathVariable("txnId") String txnId) throws Exception {
        log.info("redirect");
        return new RedirectView("http://localhost:4200/");
    }
}
