package com.builderssas.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        var template = new RetryTemplate();

        var policy = new SimpleRetryPolicy(3);            // 3 intentos
        var backoff = new FixedBackOffPolicy();           // espera fija
        backoff.setBackOffPeriod(500);                    // 500 ms

        template.setRetryPolicy(policy);
        template.setBackOffPolicy(backoff);

        return template;
    }
}
