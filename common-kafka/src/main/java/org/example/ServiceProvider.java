package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.consumer.KafkaService;

import java.util.Map;
import java.util.concurrent.Callable;

public class ServiceProvider<T> implements Callable<Void> {
    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    private final ServiceFactory<T> factory;

    @Override
    public Void call() throws Exception {
        var service = factory.create();
        var consumerGroup = service.getConsumerGroup();
        var topic = service.getTopic();
        var patternTopic = service.getPatternTopic();

        if(patternTopic == null) {
            try (var kafkaService = new KafkaService<>(consumerGroup, topic, service::parse)) {
                kafkaService.run();
            }
        } else {
            try (var kafkaService = new KafkaService<>(
                    consumerGroup,
                    patternTopic,
                    service::parse, Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
                kafkaService.run();
            }
        }
        return null;
    }
}
