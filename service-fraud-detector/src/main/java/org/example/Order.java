package org.example;

import org.apache.kafka.common.protocol.types.Field;

import java.math.BigDecimal;

public class Order {
    public Order(String email, String orderId, BigDecimal amount) {
        this.email = email;
        this.orderId = orderId;
        this.amount = amount;
    }
    private final String email;
    private final String orderId;
    private final BigDecimal amount;

    @Override
    public String toString() {
        return "Order{" +
                "email='" + email + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getOrderId() {
        return orderId;
    }
}
