package org.example;

import org.example.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(var orderDispatcher = new KafkaDispatcher<Order>()) {

            var userEmail = Math.random() + "@email.com";

            for (int i = 0; i < 3; i++) {
                var key = userEmail;

                var orderId = UUID.randomUUID().toString();
                var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                //var userEmail = Math.random() + "@email.com";
                var orderCorrelationId = new CorrelationId(NewOrderMain.class.getSimpleName());
                var order = new Order(orderId, amount, userEmail);
                orderDispatcher.send("ECOMMERCE_NEW_ORDER", key, orderCorrelationId, order);
            }
        }
    }
}
