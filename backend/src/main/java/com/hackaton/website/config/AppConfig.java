package com.hackaton.website.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(6))
                .build();
    }

    /**
     * Пул потоков для параллельных задач (FCC + расчёт + сохранение).
     * destroyMethod гарантирует shutdown при остановке приложения.
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService importExecutor() {
        int cpu = Runtime.getRuntime().availableProcessors();
        int threads = Math.min(16, Math.max(4, cpu * 2));
        return Executors.newFixedThreadPool(threads);
    }
}