package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {
    void parse(ConsumerRecord<String, Message<T>> record);
    String getTopic();
    String getConsumerGroup();
}
