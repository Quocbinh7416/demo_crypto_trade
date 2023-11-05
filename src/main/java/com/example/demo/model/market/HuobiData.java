package com.example.demo.model.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HuobiData {
    private String symbol;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal open;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal high;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal low;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal close;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal amount;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal vol;
    private Integer count;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal bid;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal bidSize;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal ask;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal askSize;
}
