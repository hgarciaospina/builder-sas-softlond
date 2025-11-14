package com.builderssas.api.core.exception;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<HttpStatus.Series, String> COLORS = Map.of(
            HttpStatus.Series.SUCCESSFUL, GREEN,
            HttpStatus.Series.CLIENT_ERROR, YELLOW,
            HttpStatus.Series.SERVER_ERROR, RED
    );

    // Color dinámico
    private final Function<HttpStatus, String> color =
            status -> Optional.ofNullable(COLORS.get(status.series())).orElse(CYAN);

    // Limpieza de mensajes
    private final Function<Throwable, String> clean =
            ex -> Optional.ofNullable(unpack(ex).getMessage())
                    .map(msg ->
                            msg.replaceAll("(?i)(error:|exception:|java\\.[a-z.]+:)", "").trim()
                    )
                    .filter(m -> !m.isBlank())
                    .orElse("Error interno del servidor");

    // DESANIDAR COMPLETAMENTE FUNCIONAL
    private static Throwable unpack(Throwable e) {
        return Stream.iterate(Optional.ofNullable(e), opt ->
                        opt.flatMap(th ->
                                Optional.ofNullable(th.getCause())
                                        .filter(c -> th instanceof CompletionException)
                        )
                )
                .takeWhile(Optional::isPresent)
                .map(Optional::orElseThrow)
                .reduce((prev, next) -> next)
                .orElse(e);
    }

    // Body funcional
    private Map<String, Object> body(HttpStatus status, String message) {
        var now = LocalDateTime.now();
        var col = color.apply(status);

        System.out.printf("%s[%s] %s (%d)%s%n",
                col, FORMATTER.format(now), status.getReasonPhrase(), status.value(), RESET);

        System.out.printf("%s⮞ %s%s%n", col, message, RESET);

        return Map.of(
                "status", status.value(),
                "timestamp", now.toString(),
                "message", message
        );
    }

    private Map<String, Object> body(HttpStatus status, List<String> messages) {
        return body(status, String.join(" | ", messages));
    }

    // HANDLERS
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, clean.apply(ex)));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> conflict(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, clean.apply(ex)));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> runtime(RuntimeException ex) {
        var msg = Optional.ofNullable(ex.getMessage()).orElse("Error interno del servidor");

        return Stream.of(
                        Map.entry("not found", HttpStatus.NOT_FOUND),
                        Map.entry("no encontrado", HttpStatus.NOT_FOUND),
                        Map.entry("no existe", HttpStatus.NOT_FOUND),
                        Map.entry("duplicado", HttpStatus.CONFLICT),
                        Map.entry("ya existe", HttpStatus.CONFLICT),
                        Map.entry("duplicate", HttpStatus.CONFLICT),
                        Map.entry("exists", HttpStatus.CONFLICT),
                        Map.entry("invalid", HttpStatus.BAD_REQUEST),
                        Map.entry("bad request", HttpStatus.BAD_REQUEST)
                )
                .filter(e -> msg.toLowerCase().contains(e.getKey()))
                .findFirst()
                .map(e -> ResponseEntity.status(e.getValue())
                        .body(body(e.getValue(), clean.apply(ex))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(body(HttpStatus.INTERNAL_SERVER_ERROR, clean.apply(ex))));
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            TransactionSystemException.class,
            PersistenceException.class,
            org.hibernate.exception.ConstraintViolationException.class
    })
    public ResponseEntity<Map<String, Object>> dataIntegrity(Exception ex) {

        var realMessage =
                Optional.of(unpack(ex))
                        .map(Throwable::getMessage)
                        .map(String::trim)
                        .orElse("Violación de integridad de datos.");

        System.out.printf("%s⮞ VIOLACIÓN DE INTEGRIDAD (REAL): %s%s%n",
                RED, realMessage, RESET);

        var cleaned =
                Optional.of(realMessage)
                        .map(msg -> msg.replaceAll("(?i)(org\\.hibernate\\.exception\\.[a-z.]+:)", ""))
                        .map(msg -> msg.replaceAll("\"", ""))
                        .map(String::trim)
                        .orElse("Violación de integridad de datos.");

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, cleaned));
    }

    // ❗❗❗ AQUI ESTÁ LA ÚNICA LÍNEA QUE PEDISTE ARREGLAR ❗❗❗
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> unauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)  // ← CAMBIADO DE UNAUTHORIZED a FORBIDDEN
                .body(body(HttpStatus.FORBIDDEN, clean.apply(ex)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> constraint(ConstraintViolationException ex) {
        var messages = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(HttpStatus.BAD_REQUEST, messages));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> generic(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(HttpStatus.INTERNAL_SERVER_ERROR, clean.apply(ex)));
    }

    // Excepciones dominio
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) { super(message); }
    }

    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) { super(message); }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) { super(message); }
    }
}
