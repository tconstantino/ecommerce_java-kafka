package org.example;

import java.math.BigDecimal;

public class Order {
    public Order(String userId, String orderId, String email, BigDecimal amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.email = email;
        this.amount = amount;
    }
    private final String userId;
    private final String orderId;
    private final String email;
    private final BigDecimal amount;

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Order{" +
                "userId='" + userId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                "}";
    }
}
