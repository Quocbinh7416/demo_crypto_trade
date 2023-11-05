package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "users_balance_data")
@Data
public class UserBalanceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "coin_id")
    private Long coinId;

    @Column(precision = 20, scale = 8)
    private BigDecimal amount;
}
