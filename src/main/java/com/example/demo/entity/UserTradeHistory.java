package com.example.demo.entity;

import com.example.demo.model.TradeRequest;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "users_trade_history")
@Data
@NoArgsConstructor
public class UserTradeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "coin_id")
    private Long coinId;

    @Column(name = "amount", precision = 20, scale = 8)
    private BigDecimal amount;

    @Column(name = "is_buy")
    private boolean isBuy;

    @Column(name = "timestamp")
    private Instant timestamp;

    @Column(name = "trade_market")
    private String tradeMarket;

    @Column(name = "trade_market_quantity", precision = 20, scale = 8)
    private BigDecimal tradeMarketQuantity;

    @Column(name = "trade_market_price", precision = 20, scale = 8)
    private BigDecimal tradeMarketPrice;

    @Column(name = "trade_market_timestamp")
    private Instant tradeMarketTimestamp;

    public UserTradeHistory(TradeRequest request, boolean isBuy, Instant timestamp, String tradeMarket,
                            BigDecimal tradeMarketQuantity, BigDecimal tradeMarketPrice, Instant tradeMarketTimestamp) {
        this.userId = request.getUserId();
        this.coinId = request.getCryptoId();
        this.amount = request.getAmount();
        this.isBuy = isBuy;
        this.timestamp = timestamp;
        this.tradeMarket = tradeMarket;
        this.tradeMarketQuantity = tradeMarketQuantity;
        this.tradeMarketPrice = tradeMarketPrice;
        this.tradeMarketTimestamp = tradeMarketTimestamp;
    }
}
