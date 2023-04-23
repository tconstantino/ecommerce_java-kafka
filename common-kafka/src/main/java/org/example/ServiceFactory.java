package org.example;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
