package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class FraudDetectorService implements ConsumerService<Order> {
    private FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExists("CREATE TABLE ORDERS (UUID VARCHAR(200) PRIMARY KEY, IS_FRAUD BOOLEAN)");
    }

    private final LocalDatabase database;
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) {
        new ServiceRunner(FraudDetectorService::new).start(1);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("________________________________________");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        var message = record.value();
        var correlationId = message.getId().continueWith(FraudDetectorService.class.getSimpleName());
        var order = message.getPayload();
        if(wasProcessed(order)) {
            System.out.println("Order " + order.getOrderId() + " was already processed");
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //Ignoring
            e.printStackTrace();
        }

        if(isFraud(order)) {
            database.insertOrUpdate("INSERT INTO ORDERS (UUID, IS_FRAUD) VALUES (?, TRUE)", order.getOrderId());
            //Pretending thar the fraud happens when the amount is >= 4500
            System.out.println("Order is a fraud!!!");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), correlationId, order);
        } else {
            database.insertOrUpdate("INSERT INTO ORDERS (UUID, IS_FRAUD) VALUES (?, FALSE)", order.getOrderId());
            System.out.println("Approved: " +  order);
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), correlationId, order);
        }

        System.out.println("Order processed");
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var result = database.query("SELECT UUID FROM ORDERS WHERE UUID = ? LIMIT 1", order.getOrderId());
        return result.next();
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
        return FraudDetectorService.class.getSimpleName();
    }

    private Boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
