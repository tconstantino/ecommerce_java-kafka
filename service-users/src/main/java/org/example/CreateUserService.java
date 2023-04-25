package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

public class CreateUserService implements ConsumerService<Order> {
    private final LocalDatabase database;

    private CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("CREATE TABLE USERS (UUID VARCHAR(200) PRIMARY KEY, EMAIL VARCHAR(200))");
    }

    public static void main(String[] args) {
        new ServiceRunner(CreateUserService::new).start(1);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("________________________________________");
        System.out.println("Processing new order, checking new user");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        var message = record.value();
        var order = message.getPayload();
        if(isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        } else {
            System.out.println("User " + order.getEmail() + " already exists");
        }
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
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        var uuid = UUID.randomUUID().toString();
        database.insertOrUpdate("INSERT INTO USERS (UUID, EMAIL) VALUES (?, ?)", uuid, email);
        System.out.println("User " + uuid + " and " + email + " created!");
    }

    private Boolean isNewUser(String email) throws SQLException {
        var results = database.query("SELECT UUID FROM USERS WHERE EMAIL = ? LIMIT 1", email);
        return !results.next();
    }
}
