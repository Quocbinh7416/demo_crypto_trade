package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseDTO implements Serializable {
    private Long id;
    private String name;
    private Double balanceAmount;
}
