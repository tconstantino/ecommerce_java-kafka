package org.example;

import java.math.BigDecimal;

public class Order {
    public Order(String userId, String orderId, BigDecimal amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
    }
    private final String userId;
    private final String orderId;
    private final BigDecimal amount;
}
