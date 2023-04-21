package org.example;

import java.util.UUID;

public class CorrelationId {
    CorrelationId(String title) {
        id = title + "("+ UUID.randomUUID().toString() + ")";
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

    public CorrelationId continueWith(String title) {
        return new CorrelationId(id + "-" + title);
    }
}
