package com.builderssas.api.cron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * ============================================================================
 *                  CONTROLADOR MANUAL PARA PRUEBAS DE CRON (FUNCIONAL)
 * ============================================================================
 *
 * Activa los CRON reales pero con una FECHA ESPECÍFICA enviada desde Postman.
 *
 * Endpoints:
 *   GET /api/v1/cron/test/start?date=2025-11-12
 *   GET /api/v1/cron/test/finish?date=2025-11-12
 *   GET /api/v1/cron/test/delivery?date=2025-11-12
 *   GET /api/v1/cron/test/progress?date=2025-11-12
 *   GET /api/v1/cron/test/all?date=2025-11-12
 *
 * Cada uno llama a los métodos manuales de ProjectCronTasks:
 *   - runStartCron(date)
 *   - runFinishCron(date)
 *   - runDeliveryCron(date)
 *   - runProgressCron(date)
 * ============================================================================
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/cron/test")
@RequiredArgsConstructor
public class CronTestController {

    private final ProjectCronTasks cron;

    // -------------------------------------------------------------
    // ✅ Resolver fecha con interfaz funcional; null/blank -> hoy
    //     Formato inválido -> IllegalArgumentException (igual que tu versión)
    // -------------------------------------------------------------
    private LocalDate resolveDate(final String date) {
        return Optional.ofNullable(date)
                .filter(d -> !d.isBlank())
                .map(this::parseOrThrow)          // si invalida -> lanza IllegalArgumentException
                .orElseGet(LocalDate::now);       // si null/blank -> hoy
    }

    private LocalDate parseOrThrow(final String raw) {
        try {
            return LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            log.error("Fecha inválida recibida: {}", raw);
            throw new IllegalArgumentException("Formato de fecha inválido. Use: yyyy-MM-dd");
        }
    }

    // -------------------------------------------------------------
    // Función auxiliar genérica (evita duplicación)
    // -------------------------------------------------------------
    private Map<String, Object> executeCron(
            final String action,
            final String date,
            final Function<LocalDate, Void> cronFunction
    ) {
        final LocalDate target = resolveDate(date);

        Optional.ofNullable(cronFunction)
                .ifPresent(fn -> fn.apply(target));

        return Map.of(
                "action", action,
                "date_used", target.toString(),
                "status", "OK"
        );
    }

    // -------------------------------------------------------------
    // START (PENDING → IN_PROGRESS)
    // -------------------------------------------------------------
    @GetMapping("/start")
    public Map<String, Object> start(@RequestParam(required = false) final String date) {
        return executeCron(
                "PENDING → IN_PROGRESS",
                date,
                d -> { cron.runStartCron(d); return null; }
        );
    }

    // -------------------------------------------------------------
    // ✅ FINISH (IN_PROGRESS → FINISHED)
    // -------------------------------------------------------------
    @GetMapping("/finish")
    public Map<String, Object> finish(@RequestParam(required = false) final String date) {
        return executeCron(
                "IN_PROGRESS → FINISHED",
                date,
                d -> { cron.runFinishCron(d); return null; }
        );
    }

    // -------------------------------------------------------------
    // DELIVERY (update projectEndDate)
    // -------------------------------------------------------------
    @GetMapping("/delivery")
    public Map<String, Object> delivery(@RequestParam(required = false) final String date) {
        return executeCron(
                "PROJECT DELIVERY DATE UPDATED",
                date,
                d -> { cron.runDeliveryCron(d); return null; }
        );
    }

    // -------------------------------------------------------------
    // PROGRESS (recalcular % de proyecto)
    // -------------------------------------------------------------
    @GetMapping("/progress")
    public Map<String, Object> progress(@RequestParam(required = false) final String date) {
        return executeCron(
                "PROJECT PROGRESS UPDATED",
                date,
                d -> { cron.runProgressCron(d); return null; }
        );
    }

    // -------------------------------------------------------------
    // ALL (todo el ciclo)
    // -------------------------------------------------------------
    @GetMapping("/all")
    public Map<String, Object> all(@RequestParam(required = false) final String date) {
        final LocalDate target = resolveDate(date);

        Stream.<Function<LocalDate, Void>>of(
                d -> { cron.runStartCron(d); return null; },
                d -> { cron.runFinishCron(d); return null; },
                d -> { cron.runDeliveryCron(d); return null; },
                d -> { cron.runProgressCron(d); return null; }
        ).forEach(fn -> fn.apply(target));

        return Map.of(
                "action", "ALL CRONS EXECUTED",
                "date_used", target.toString(),
                "status", "OK"
        );
    }
}