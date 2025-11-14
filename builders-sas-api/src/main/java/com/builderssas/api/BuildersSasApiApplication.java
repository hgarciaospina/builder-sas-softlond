package com.builderssas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Application entry point for builders-sas-api.
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.builderssas")
public class BuildersSasApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuildersSasApiApplication.class, args);
    }
}
@RestController
class TestPing {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}