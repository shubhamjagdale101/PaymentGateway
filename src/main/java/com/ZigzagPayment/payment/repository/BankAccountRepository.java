package com.ZigzagPayment.payment.repository;

import com.ZigzagPayment.payment.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    BankAccount findByAccountNumber(String accountNumber);
}
