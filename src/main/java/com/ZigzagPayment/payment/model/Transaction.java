package com.ZigzagPayment.payment.model;

import com.ZigzagPayment.payment.constant.CardType;
import com.ZigzagPayment.payment.constant.PaymentStatus;
import com.ZigzagPayment.payment.constant.TxnStatus;
import com.ZigzagPayment.payment.dto.TxnDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Changed to AUTO or UUID as needed
    private String transactionId; // Unique identifier for the transaction

    @ManyToOne
    @JoinColumn(name = "sender_account", referencedColumnName = "accountNumber")
    private BankAccount sender; // Changed to ManyToOne

    @ManyToOne
    @JoinColumn(name = "receiver_account", referencedColumnName = "accountNumber")
    private BankAccount receiver; // Changed to ManyToOne

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String callbackUrl;

    @Enumerated(value = EnumType.STRING)
    private TxnStatus status;

    @Enumerated(value = EnumType.STRING)
    private CardType paymentMethod;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    public TxnDto toDto() {
        return TxnDto.builder()
                .transactionId(this.transactionId)
                .status(this.status == TxnStatus.SUCCESS? PaymentStatus.PAID : PaymentStatus.UNPAID) // Ensure the type matches with TxnDto
                .paymentMethod(this.paymentMethod.toString()) // Convert Enum to String
                .build();
    }
}
