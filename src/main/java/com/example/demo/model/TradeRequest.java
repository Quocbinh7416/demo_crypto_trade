package com.example.demo.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class TradeRequest {
    @NonNull
    private Long userId;
    private Long coinId;
    private String amount;
    private boolean isSold;

}
