package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dispatcher.KafkaDispatcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class BatchSendMessageService implements ConsumerService<String> {
    BatchSendMessageService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("CREATE TABLE USERS (UUID VARCHAR(200) PRIMARY KEY, EMAIL VARCHAR(200))");
    }
    private final LocalDatabase database;
    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) {
        new ServiceRunner(BatchSendMessageService::new).start(1);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<String>> record) throws SQLException, ExecutionException, InterruptedException {
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

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_MESSAGE_TO_ALL_USES";
    }

    @Override
    public Pattern getPatternTopic() {
        return null;
    }

    @Override
    public String getConsumerGroup() {
        return BatchSendMessageService.class.getSimpleName();
    }

    private ArrayList<User> getAllUsers() throws SQLException {
        var results = database.query("SELECT UUID FROM USERS");
        var users = new ArrayList<User>();
        while (results.next()){
            users.add(new User(results.getString("UUID")));
        }
        return users;
    }
}
