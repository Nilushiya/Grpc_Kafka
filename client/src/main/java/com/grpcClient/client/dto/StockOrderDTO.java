package com.grpcClient.client.dto;

import lombok.Data;

@Data
public class StockOrderDTO {
    private String orderId;
    private String stockSymbol;
    private String orderType;
    private double price;
    private int quantity;
}
