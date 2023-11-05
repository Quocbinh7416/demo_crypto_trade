package com.example.demo.entity;

import com.example.demo.model.market.MarketInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "trade_market_data")
@Data
@NoArgsConstructor
public class TradeMarketData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coin_id")
    private Long coinId;

    @Column(name = "bid_trade_market")
    private String bidTradeMarket;

    @Column(name = "bid_price", precision = 20, scale = 8)
    private BigDecimal bidPrice;

    @Column(name = "bid_quantity", precision = 20, scale = 8)
    private BigDecimal bidQuantity;

    @Column(name = "ask_trade_market")
    private String askTradeMarket;

    @Column(name = "ask_price", precision = 20, scale = 8)
    private BigDecimal askPrice;

    @Column(name = "ask_quantity", precision = 20, scale = 8)
    private BigDecimal askQuantity;

    @Column(name = "timestamp")
    private Instant timestamp;

    public TradeMarketData(Long coinId, List<MarketInfo> marketInfoList) {
        this.coinId = coinId;

        // find max for sell - bid
        MarketInfo max = marketInfoList.stream().max(Comparator.comparing(MarketInfo::getBidPrice)).get();
        this.bidTradeMarket = max.getMarketName();
        this.bidPrice = max.getBidPrice();
        this.bidQuantity = max.getBidQty();
        // find min for buy - ask
        MarketInfo min = marketInfoList.stream().min(Comparator.comparing(MarketInfo::getBidPrice)).get();
        this.askTradeMarket = min.getMarketName();
        this.askPrice = min.getAskPrice();
        this.askQuantity = min.getAskQty();
    }
}
