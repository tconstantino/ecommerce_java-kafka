package org.example;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdpater implements JsonSerializer<Message>, JsonDeserializer<Message> {
    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", message.getPayload().getClass().getName());
        obj.add("correlationId", context.serialize(message.getId()));
        obj.add("payload", context.serialize(message.getPayload()));

        return obj;
    }

    @Override
    public Message deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        var payloadType =obj.get("type").getAsString();
        var correlationId = (CorrelationId) context.deserialize(obj.get("correlationId"), CorrelationId.class);
        try {
            // Maybe you want to use an "accept list"
            var payload = context.deserialize(obj.get("payload"), Class.forName(payloadType));

            return new Message(correlationId, payload);
        } catch (ClassNotFoundException e) {
            // You might want to deal with this exception
            throw new JsonParseException(e);
        }
    }
}
