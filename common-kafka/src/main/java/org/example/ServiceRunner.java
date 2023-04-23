package org.example;

import java.util.concurrent.Executors;

public class ServiceRunner<T> {
    public ServiceRunner(ServiceFactory<T> factory) {
        this.provider = new ServiceProvider(factory);
    }

    private final ServiceProvider<T> provider;

    public void start(int threadCount) {
        var pool = Executors.newFixedThreadPool(threadCount);
        for(int i=0; i<=threadCount; i++) {
            pool.submit(provider);
        }
    }
}
