package org.example;

import java.math.BigDecimal;

public class Order {
    public Order(String orderId, String email, BigDecimal amount) {
        this.orderId = orderId;
        this.email = email;
        this.amount = amount;
    }
    private final String orderId;
    private final String email;
    private final BigDecimal amount;

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", email='" + email + '\'' +
                ", amount=" + amount +
                '}';
    }
}
