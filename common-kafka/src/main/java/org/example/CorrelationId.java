package org.example;

import java.util.UUID;

public class CorrelationId {
    CorrelationId() {
        id = UUID.randomUUID().toString();
    }

    private final String id;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CorrelationId{" +
                "id='" + id + '\'' +
                '}';
    }
}
