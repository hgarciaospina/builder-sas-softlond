package com.builderssas.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationConfig {

    private String webhookUrl;
    private boolean externalEnabled = true;
}
