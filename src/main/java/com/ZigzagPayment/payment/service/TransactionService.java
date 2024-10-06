package com.ZigzagPayment.payment.service;

import com.ZigzagPayment.payment.config.JwtUtil;
import com.ZigzagPayment.payment.constant.CardType;
import com.ZigzagPayment.payment.constant.TxnStatus;
import com.ZigzagPayment.payment.dto.InitiatePaymentDto;
import com.ZigzagPayment.payment.dto.PaymentDto;
import com.ZigzagPayment.payment.model.*;
import com.ZigzagPayment.payment.repository.BankAccountRepository;
import com.ZigzagPayment.payment.repository.CardRepository;
import com.ZigzagPayment.payment.repository.OtpRepository;
import com.ZigzagPayment.payment.repository.TransactionRepository;
import com.ZigzagPayment.payment.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class TransactionService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private NetworkCalls networkCalls;
    @Value("${otp.Expiration.time}")
    private long expirationTime;

    public ApiResponse<Object> processInitiatePayment(InitiatePaymentDto payment) throws Exception {
        BankAccount account = bankAccountRepository.findByAccountNumber(payment.getReceiverAccount());
        if(account == null) throw new Exception("there is no account with given account number");

        Transaction transaction = Transaction.builder()
                .amount(payment.getTotalAmount())
                .receiver(account)
                .status(TxnStatus.INITIATED)
                .callbackUrl(payment.getCallbackUrl())
                .build();
        transaction = transactionRepository.save(transaction);
        String txnId = transaction.getTransactionId();
        return ApiResponse.success(transaction.getTransactionId(), "http://localhost:8081/payment/" + txnId, HttpStatus.OK.value());
    }

    public String handlePaymentInterface(Model model, String txnId) {
        Transaction txn = transactionRepository.findByTransactionId(txnId);
        model.addAttribute("receiverAccount", txn.getReceiver().getAccountNumber());
        model.addAttribute("totalAmount", txn.getAmount());
        model.addAttribute("recipientName", txn.getReceiver().getName());
        return "payment";
    }

    public String sendOtpToSenderEmail(String senderCardNumber) throws Exception {
        log.info(senderCardNumber);
        Card card = cardRepository.findByCardNumber(senderCardNumber);
        if(card == null) {
            log.info("exception bro");
            throw new Exception("Account with this account number is not found");
        }

        log.info("2");
        SecureRandom random = new SecureRandom();
        String generatedOtp = String.format("%06d", random.nextInt(999999));

        log.info(generatedOtp);
        Otp otp = otpRepository.findByKey(card.getCardNumber());
        if(otp != null){
            otp.setValue(generatedOtp);
        }
        else{
            otp = Otp.builder()
                    .key(card.getCardNumber())
                    .value(generatedOtp)
                    .build();
        }
        log.info(String.valueOf(otp));
        otp = otpRepository.save(otp);
        emailService.sendOtpMail(card.getAccount().getEmail(), card.getAccount().getName(), generatedOtp);
        return "Otp send Successfully";
    }

    private Boolean verifyOtp(String key, String customerOtp){
        Otp otp = otpRepository.findByKey(key);
        long duration = Duration.between(Instant.now(), otp.getUpdatesAt().toInstant()).toMillis();

        return duration <= expirationTime && otp.getValue().equals(customerOtp);
    }

    private void makeCallback(Transaction txn){
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", jwtUtil.generateToken("server", "ADMIN"));
        Map result = networkCalls.makePostCall(txn.getCallbackUrl(), txn.toDto(), headers);
    }

    private ApiResponse<Object> debitAndCreditTransaction(PaymentDto paymentDto) throws Exception {
        Transaction txn = transactionRepository.findByTransactionId(paymentDto.getTxnId());
        if(txn == null) throw new Exception("No Transaction with the given Transaction Id found.");
        BankAccount receiverAccount = txn.getReceiver();

        log.info(paymentDto.getCardNumber());
        Card card = cardRepository.findByCardNumber(paymentDto.getCardNumber());
        if(card == null) throw new Exception("No Card found With the given cardNumber.");

        if(!Objects.equals(card.getCvv(), paymentDto.getCvv())) throw new Exception("Credentials Doesn't Match");
        if(!Objects.equals(card.getExpiration(), paymentDto.getExpiryDate())) throw new Exception("Credentials Doesn't Match");
        if(card.getType() != paymentDto.getPaymentType()) throw new Exception("Credentials Doesn't Match");

        CardType cardType = card.getType();
        String resMsg = null;
        if(txn.getAmount() > card.getAccount().getTransactionLimit()) {
            resMsg = "Amount is greater than ur transaction limit.";
            txn.setStatus(TxnStatus.FAILED);
            txn = transactionRepository.save(txn);
            return ApiResponse.success(txn, resMsg, HttpStatus.OK.value());
        }

        if(cardType == CardType.CREDIT_CARD){
            if(card.getAccount().getAmount() + card.getLimitAmount() >= txn.getAmount()) {
                resMsg = "Payment Done";
            }
            else {
                resMsg = "Payment Limit Exceeded";
            }
        } else if(cardType == CardType.DEBIT_CARD){
            if(txn.getAmount() <= card.getAccount().getAmount()) {
                resMsg = "Payment Done";
            }
            else{
                resMsg = "Insufficient Balance";
            }
        }

        BankAccount bankAccount = card.getAccount();
        bankAccount.setAmount(bankAccount.getAmount() - txn.getAmount());
        bankAccountRepository.save(bankAccount);
        txn.setPaymentMethod(cardType);
        txn.setStatus(TxnStatus.SUCCESS);
        txn.setSender(card.getAccount());
        txn = transactionRepository.save(txn);
        receiverAccount.setAmount(receiverAccount.getAmount() + txn.getAmount());
        makeCallback(txn);
        return ApiResponse.success(txn, resMsg, HttpStatus.OK.value());
    }

    public ApiResponse<Object> makePayment(PaymentDto paymentDto) throws Exception {
        Transaction txn = transactionRepository.findByTransactionId(paymentDto.getTxnId());
        Boolean verifyOtpResult = verifyOtp(paymentDto.getCardNumber(), paymentDto.getOtp());
        if(!verifyOtpResult) {
            return ApiResponse.success("", "Incorrect Otp", HttpStatus.OK.value());
        }

        ApiResponse<Object> resObject = debitAndCreditTransaction(paymentDto);
        return  resObject;
    }
}
