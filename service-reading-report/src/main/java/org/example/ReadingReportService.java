package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public class ReadingReportService {
    private final static Path SOURCE = new File("/src/main/resources/reports.txt").toPath();
    private final KafkaDispatcher<User> orderDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) {
        var readingReportService = new ReadingReportService();
        try(var service = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "USER_GENERATE_READING_REPORT",
                readingReportService::parse,
                User.class)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, User> record) throws IOException {
        System.out.println("________________________________________");
        System.out.println("Processing report for " + record.value());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        var user = record.value();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUuid());

        System.out.println("File created: " + target.getAbsolutePath());
    }
}