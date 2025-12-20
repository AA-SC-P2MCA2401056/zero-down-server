package com.server.zero_down.Common.Handler.Scheduler;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class SensorExecutorShutdown {

    private final ExecutorService executor;

    public SensorExecutorShutdown(ExecutorService executor) {
        this.executor = executor;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        System.out.println("Sensor executor shut down cleanly");
    }
}

