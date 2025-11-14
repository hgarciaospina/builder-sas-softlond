package com.builderssas.api.cron;

import com.builderssas.api.domain.model.construction.ConstructionOrder;
import com.builderssas.api.domain.model.enums.OrderStatus;
import com.builderssas.api.repository.ConstructionOrderRepository;
import com.builderssas.api.repository.ProjectRepository;
import com.builderssas.api.services.notification.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ======================================================================
 *              CRON PRINCIPAL DEL SISTEMA (REFACTORED)
 * ======================================================================
 *
 *  Ahora cada CRON tiene:
 *      - Versión programada (hora real del día)
 *      - Versión manual (acepta fecha enviada por controlador)
 *
 *  Esto permite pruebas controladas sin afectar el cron real.
 *
 * ======================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectCronTasks {

    private final ConstructionOrderRepository orderRepo;
    private final ProjectRepository projectRepo;
    private final NotificationService notificationService;

    // ======================================================================
    // 1. PENDING → IN_PROGRESS
    // ======================================================================

    /**  Versión real (se ejecuta 06:00 AM) */
    @Scheduled(cron = "0 0 6 * * *", zone = "America/Bogota")
    @Transactional
    public void setOrdersInProgressEachMorning() {
        runStartCron(LocalDate.now());
    }

    /** Versión manual para pruebas */
    @Transactional
    public void runStartCron(LocalDate date) {

        log.info("[CRON START] Buscando órdenes con startDate={} y estado=PENDING", date);

        List<ConstructionOrder> toStart =
                orderRepo.findByScheduledStartDate(date).stream()
                        .filter(o -> o.getOrderStatus() == OrderStatus.PENDING)
                        .toList();

        toStart.forEach(order -> {
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
            orderRepo.save(order);

            log.info("[CRON START] Orden {} → IN_PROGRESS", order.getId());
        });

        log.info("[CRON START] Finalizado. Total: {}", toStart.size());
    }

    // ======================================================================
    // 2. IN_PROGRESS → FINISHED
    // ======================================================================

    /**  Versión real 23:00 PM */
    @Scheduled(cron = "0 0 23 * * *", zone = "America/Bogota")
    @Transactional
    public void finalizeOrdersEachNight() {
        runFinishCron(LocalDate.now());
    }

    /** ✅ Versión manual */
    @Transactional
    public void runFinishCron(LocalDate date) {

        log.info("[CRON FINISH] Buscando órdenes IN_PROGRESS con endDate={}", date);

        List<ConstructionOrder> ending =
                orderRepo.findByScheduledEndDate(date).stream()
                        .filter(o -> o.getOrderStatus() == OrderStatus.IN_PROGRESS)
                        .toList();

        ending.forEach(order -> {
            order.setOrderStatus(OrderStatus.FINISHED);
            orderRepo.save(order);

            log.info("[CRON FINISH] Orden {} FINALIZADA", order.getId());
        });

        log.info("[CRON FINISH] Finalizado. Total: {}", ending.size());
    }

    // ======================================================================
    // 3. Actualizar projectEndDate
    // ======================================================================

    /** Versión real (23:05 PM) */
    @Scheduled(cron = "0 5 23 * * *", zone = "America/Bogota")
    @Transactional
    public void updateProjectDeliveryDates() {
        runDeliveryCron(LocalDate.now());
    }

    /** Versión manual */
    @Transactional
    public void runDeliveryCron(LocalDate date) {

        log.info("[CRON DELIVERY] Recalculando entrega para proyectos afectados en {}", date);

        List<Long> projectIds =
                orderRepo.findByScheduledEndDate(date).stream()
                        .filter(o -> o.getOrderStatus() == OrderStatus.FINISHED)
                        .map(o -> o.getProject().getId())
                        .distinct()
                        .toList();

        projectIds.forEach(pid -> {

            LocalDate lastEnd =
                    orderRepo.findLastEndDate(pid)
                            .map(d -> d.plusDays(1))
                            .orElse(null);

            projectRepo.findById(pid).ifPresent(project -> {
                project.setProjectEndDate(lastEnd);
                projectRepo.save(project);

                log.info("[CRON DELIVERY] Proyecto {} → nueva entrega = {}", pid, lastEnd);
            });
        });

        log.info("[CRON DELIVERY] Finalizado. {} proyectos actualizados.", projectIds.size());
    }

    // ======================================================================
    // 4. Actualizar progressPercentage
    // ======================================================================

    /**  Versión real (23:10 PM) */
    @Scheduled(cron = "0 10 23 * * *", zone = "America/Bogota")
    @Transactional
    public void updateProjectProgressPercentage() {
        runProgressCron(LocalDate.now());
    }

    /**  Versión manual */
    @Transactional
    public void runProgressCron(LocalDate date) {

        log.info("[CRON PROGRESS] Recalculando progreso para todos los proyectos…");

        projectRepo.findAll().forEach(project -> {

            long total = orderRepo.countByProject(project.getId());

            if (total == 0) return;

            long finished = orderRepo.countFinishedByProject(project.getId());
            double progress = (finished * 100.0) / total;

            project.setProgressPercentage(progress);
            projectRepo.save(project);

            log.info("[CRON PROGRESS] Proyecto {} → {}%", project.getId(), progress);
        });

        log.info("[CRON PROGRESS] Finalizado.");
    }
}
