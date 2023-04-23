package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService implements ConsumerService<Email>{
    public static void main(String[] args) {
        new ServiceRunner(EmailService::new).start(5);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Email>> record) {
        System.out.println("________________________________________");
        System.out.println("Sending email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //ignoring
            e.printStackTrace();
        }
        System.out.println("Email sent");
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }
}
