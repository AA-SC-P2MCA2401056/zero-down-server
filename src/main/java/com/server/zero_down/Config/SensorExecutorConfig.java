package com.server.zero_down.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SensorExecutorConfig {

    /**
     * Fixed thread pool for logical sensors
     * TEMP, HUM now → more later
     */
    @Bean
    public ExecutorService sensorExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}

