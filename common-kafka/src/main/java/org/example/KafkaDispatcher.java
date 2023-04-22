package org.example;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {
    KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }
    private final KafkaProducer<String, Message<T>> producer;
    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {
        var future = sendAsync(topic, key, id, payload);
        future.get();
    }

    Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId id, T payload) {
        var value = new Message<T>(id, payload);
        var record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("Success sending " + data.topic() + ":::partition " + data.partition() + "/ offset " + data.offset() + "/" + data.timestamp());
        };
        var future = producer.send(record, callback);
        return future;
    }

    @Override
    public void close() {
        producer.close();
    }
}
