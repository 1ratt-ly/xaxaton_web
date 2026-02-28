package com.hackaton.website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // ms
        factory.setReadTimeout(6000); // ms
        return new RestTemplate(factory);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService importExecutor() {
        int cpu = Runtime.getRuntime().availableProcessors();
        int threads = Math.min(16, Math.max(4, cpu * 2));
        return Executors.newFixedThreadPool(threads);
    }
}