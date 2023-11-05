package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradeMarketDTO {
    private Long cryptoId;
    private String cryptoName;
    private String symbol;
    private String bidTradeMarket;
    private String bidPrice;
    private String bidQuantity;
    private String askTradeMarket;
    private String askPrice;
    private String askQuantity;
    private String timestamp;
}
