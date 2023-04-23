package org.example;

import org.example.consumer.KafkaService;

import java.util.concurrent.Callable;

public class ServiceProvider<T> implements Callable<Void> {
    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    private final ServiceFactory<T> factory;

    @Override
    public Void call() {
        var service = factory.create();
        var consumerGroup = service.getConsumerGroup();
        var topic = service.getTopic();

        try(var kafkaService = new KafkaService<>(consumerGroup, topic, service::parse)){
            kafkaService.run();
        }
        return null;
    }
}
