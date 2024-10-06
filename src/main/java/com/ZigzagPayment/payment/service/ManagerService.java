package com.ZigzagPayment.payment.service;

import com.ZigzagPayment.payment.constant.AccountStatus;
import com.ZigzagPayment.payment.dto.CreateBankAccountDto;
import com.ZigzagPayment.payment.dto.CreateCardDto;
import com.ZigzagPayment.payment.model.BankAccount;
import com.ZigzagPayment.payment.model.Card;
import com.ZigzagPayment.payment.repository.BankAccountRepository;
import com.ZigzagPayment.payment.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ManagerService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private CardRepository cardRepository;

    private String generateRandomNumber(){
        SecureRandom random = new SecureRandom();
        long generatedOtp = random.nextLong(1_000_000_000_000_0000L);
        return String.format("%016d", Math.abs(generatedOtp));
    }

    private String getBankAccountNumber(){
        String generatedAccountNumber = generateRandomNumber();
        BankAccount account = bankAccountRepository.findByAccountNumber(generatedAccountNumber);

        if(account == null) return generatedAccountNumber;
        return getBankAccountNumber();
    }

    private String getCardNumber(){
        String generatedCardNumber = generateRandomNumber();
        Card card = cardRepository.findByCardNumber(generatedCardNumber);

        if(card == null) return generatedCardNumber;
        return getCardNumber();
    }

    public BankAccount createBankAccount(CreateBankAccountDto createBankAccountDto) {
        BankAccount bankAccount = BankAccount.builder()
                .accountNumber(getBankAccountNumber())
                .name(createBankAccountDto.getName())
                .amount(5000)
                .email(createBankAccountDto.getEmail())
                .status(AccountStatus.ACTIVE)
                .build();
        return bankAccountRepository.save(bankAccount);
    }

    public Card createCard(CreateCardDto createCardDto) throws Exception {
        BankAccount account = bankAccountRepository.findByAccountNumber(createCardDto.getBankAccountNumber());
        if(account == null) throw new Exception("Account doest not exist with given account number");

        Card card = Card.builder()
                .cardNumber(getCardNumber())
                .cvv("123")
                .expiration("10/25")
                .type(createCardDto.getCardType())
                .account(account)
                .limitAmount(50000)
                .build();
        return cardRepository.save(card);
    }
}
