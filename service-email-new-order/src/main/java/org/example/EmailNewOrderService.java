package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dispatcher.KafkaDispatcher;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class EmailNewOrderService implements ConsumerService<Order> {
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) {
        new ServiceRunner(EmailNewOrderService::new).start(1);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
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

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public Pattern getPatternTopic() {
        return null;
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
