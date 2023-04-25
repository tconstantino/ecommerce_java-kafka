package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class LogService implements ConsumerService<String> {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
       new ServiceRunner(LogService::new).start(3);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("________________________________________");
        System.out.println("LOG: " + record.topic());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
    }

    @Override
    public String getTopic() {
        return null;
    }

    @Override
    public Pattern getPatternTopic() {
        return Pattern.compile("ECOMMERCE.*");
    }

    @Override
    public String getConsumerGroup() {
        return LogService.class.getSimpleName();
    }
}
