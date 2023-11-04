package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "crypto_coin")
@Data
public class CryptoPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    @Column(name = "bid_price")
    private double bidPrice;
    @Column(name = "bid_quantity")
    private double bidQuantity;
    @Column(name = "ask_price")
    private double askPrice;
    @Column(name = "ask_quantity")
    private double askQuantity;
}
