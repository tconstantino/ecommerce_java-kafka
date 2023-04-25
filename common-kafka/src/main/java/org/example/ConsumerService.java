package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public interface ConsumerService<T> {
    void parse(ConsumerRecord<String, Message<T>> record) throws IOException, ExecutionException, InterruptedException, SQLException;
    String getTopic();
    Pattern getPatternTopic();
    String getConsumerGroup();
}
