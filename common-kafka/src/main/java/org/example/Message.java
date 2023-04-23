package org.example;

public class Message<T> {
    public Message(CorrelationId id, T payload) {
        this.id = id;
        this.payload = payload;
    }

    private final CorrelationId id;
    private final T payload;

    public CorrelationId getId() {
        return id;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", payload=" + payload +
                '}';
    }
}
