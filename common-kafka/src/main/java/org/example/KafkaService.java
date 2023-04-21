package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse) {
        this(groupId, parse, Map.of());
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(groupId, parse, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(String groupId, ConsumerFunction<T> parse, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, properties));
    }

    void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() + " registro(s)");
                records.forEach((record) -> {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        // Only catches Exception because no matter which Exception
                        // I want to recover and parse the next one
                        // So far, just logging the exception for this message
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private Properties getProperties(String groupId, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
