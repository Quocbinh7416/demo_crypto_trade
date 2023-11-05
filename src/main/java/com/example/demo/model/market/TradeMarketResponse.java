package com.example.demo.model.market;

import lombok.Data;

import java.util.List;

@Data
public class TradeMarketResponse {
    List<HuobiData> huobiData;
    List<BinanceData> binanceData;
}
