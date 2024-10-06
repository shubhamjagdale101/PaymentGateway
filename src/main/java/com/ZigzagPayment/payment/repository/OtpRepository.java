package com.ZigzagPayment.payment.repository;

import com.ZigzagPayment.payment.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, String> {
    Otp findByKey(String key);
}
