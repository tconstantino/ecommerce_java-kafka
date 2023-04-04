
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.regex.Pattern;

public class LogService {
    public static void main(String[] args) {
        var logService = new LogService();
        try(var service = new KafkaService<>(LogService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                String.class,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("________________________________________");
        System.out.println("LOG: " + record.topic());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
    }
}
