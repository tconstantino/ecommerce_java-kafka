package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {
    CreateUserService() throws SQLException {
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
    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var createUserService = new CreateUserService();
        try(var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
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
