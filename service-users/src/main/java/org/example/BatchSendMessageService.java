package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {
    BatchSendMessageService() throws SQLException {
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
    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) throws SQLException {
        var batchSendMessageService = new BatchSendMessageService();
        try(var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USES",
                batchSendMessageService::parse)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException, ExecutionException, InterruptedException {
        System.out.println("________________________________________");
        System.out.println("Processing new batch...");

        var message = record.value();
        System.out.println("Topic: " + message.getPayload());
        System.out.println(record.partition());
        System.out.println(record.offset());

        for(User user:getAllUsers()) {
            var correlationId = message.getId().continueWith(BatchSendMessageService.class.getSimpleName());
            userDispatcher.send(message.getPayload(), user.getUuid(), correlationId, user);
            System.out.println("Enviei para " + user);
        }
    }

    private ArrayList<User> getAllUsers() throws SQLException {
        var query = connection.prepareStatement("SELECT UUID FROM USERS");
        var results = query.executeQuery();
        var users = new ArrayList<User>();
        while (results.next()){
            users.add(new User(results.getString("UUID")));
        }
        return users;
    }
}
