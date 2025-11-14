package com.builderssas.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuración central del scheduler de CRON.
 *
 * - @EnableScheduling habilita todos los @Scheduled de la aplicación.
 * - ThreadPoolTaskScheduler garantiza ejecución concurrente.
 * - Evita bloqueos en tareas largas y permite múltiples CRONs simultáneos.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public TaskScheduler cronTaskScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(6);                      // Número de hilos para correr CRONs concurrentes
        scheduler.setThreadNamePrefix("cron-");        // Prefijo para identificar los hilos en logs
        scheduler.setRemoveOnCancelPolicy(true);       // Limpia tareas canceladas
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);

        scheduler.initialize();
        return scheduler;
    }
}
