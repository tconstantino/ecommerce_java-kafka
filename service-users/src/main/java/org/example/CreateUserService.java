package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.consumer.KafkaService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

public class CreateUserService implements ConsumerService<Order> {
    private CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:service-users/target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("CREATE TABLE USERS (UUID VARCHAR(200) PRIMARY KEY, EMAIL VARCHAR(200))");
        } catch (SQLException e) {
            // Be careful, the sql could be wrong, be really careful
            e.printStackTrace();
        }
    }
    private final Connection connection;

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
        var insert = connection.prepareStatement("INSERT INTO USERS (UUID, EMAIL) VALUES (?, ?)");
        var uuid = UUID.randomUUID().toString();
        insert.setString(1, uuid);
        insert.setString(2, email);
        insert.execute();
        System.out.println("User " + uuid + " and " + email + " created!");

    }

    private Boolean isNewUser(String email) throws SQLException {
        var query = connection.prepareStatement("SELECT UUID FROM USERS WHERE EMAIL = ? LIMIT 1");
        query.setString(1, email);
        var results = query.executeQuery();
        return !results.next();
    }
}
