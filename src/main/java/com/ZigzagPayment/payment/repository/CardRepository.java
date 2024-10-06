package com.ZigzagPayment.payment.repository;

import com.ZigzagPayment.payment.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, String> {
    Card findByCardNumber(String senderCardNumber);
}
