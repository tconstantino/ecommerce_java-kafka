package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.consumer.KafkaService;
import org.example.dispatcher.KafkaDispatcher;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) {
        var emailNewOrderService = new EmailNewOrderService();
        try(var service = new KafkaService<>(EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                emailNewOrderService::parse)) {
            service.run();
        }
    }
    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("________________________________________");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        var message = record.value();
        var order = message.getPayload();
        var key = UUID.randomUUID().toString();
        var correlationId = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        var subject = "New order";
        var body = "Thank you for your order! We are processing your order!";
        var emailCode = new Email(order.getEmail(), subject, body);
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", key, correlationId, emailCode);

        System.out.println("Email prepared");
    }
}
