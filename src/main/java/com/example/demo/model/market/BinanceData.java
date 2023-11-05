package com.example.demo.model.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BinanceData {
    private String symbol;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal bidPrice;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal bidQty;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal askPrice;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal askQty;
}
