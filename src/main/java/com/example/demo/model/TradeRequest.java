package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequest {
    @Schema(example = "1")
    private Long userId;

    @Schema(example = "0.12800000")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal amount;

    // TradeMarketDTO
    @Schema(example = "1")
    private Long cryptoId;

    @Schema(example = "houbi")
    private String bidTradeMarket;

    @Schema(example = "0.05380300")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal bidPrice;

    @Schema(example = "0.32800000")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal bidQuantity;

    @Schema(example = "binance")
    private String askTradeMarket;

    @Schema(example = "0.05380000")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal askPrice;

    @Schema(example = "20.11650000")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal askQuantity;

    @Schema(example = "2023/11/05 17:04:49")
    private String timestamp;

}
