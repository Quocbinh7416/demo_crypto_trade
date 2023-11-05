package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTradeHistoryDTO {
    private String userName;
    private String cryptoName;
    private String symbol;
    private String timestamp;
    private String tradeOption;
    private String tradeAmount;
    private String tradeMarket;
    private String tradeMarketQuantity;
    private String tradeMarketPrice;
    private String tradeMarketTimestamp;
}
