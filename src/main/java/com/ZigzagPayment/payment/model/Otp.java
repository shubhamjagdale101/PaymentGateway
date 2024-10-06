package com.ZigzagPayment.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "otp")
public class Otp {
    @Id
    @Column(name = "otp_key")
    private String key;

    @Column(nullable = false)
    private String value;

    @UpdateTimestamp
    private Date updatesAt;
}
