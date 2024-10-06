package com.ZigzagPayment.payment.model;

import com.ZigzagPayment.payment.constant.CardType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class Card {
    @Id
    private String cardNumber;

    @ManyToOne
    @JoinColumn(name = "accountNumber", referencedColumnName = "accountNumber", nullable = false)

    private BankAccount account;
    @Column(nullable = false)

    private CardType type;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private String expiration;

    @Column(nullable = false)
    private Integer limitAmount;
}
