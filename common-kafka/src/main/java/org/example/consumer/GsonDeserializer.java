package org.example.consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.Message;
import org.example.MessageAdpater;

import java.util.Map;

public class GsonDeserializer<T> implements Deserializer<Message> {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdpater()).create();

    @Override
    public Message deserialize(String s, byte[] bytes) {
        return gson.fromJson(new String(bytes), Message.class);
    }
}
