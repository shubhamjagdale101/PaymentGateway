package com.ZigzagPayment.payment.model;

import com.ZigzagPayment.payment.constant.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {
    @Id
    private String accountNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer amount; // amount mentioned is in smallest unit

    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    @Builder.Default
    private Integer transactionLimit = 5000;
}
