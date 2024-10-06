package com.ZigzagPayment.payment.repository;

import com.ZigzagPayment.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Transaction findByTransactionId(String txnId);
}
