package com.builderssas.api.controller.constructionrequest;

import com.builderssas.api.core.exception.GlobalExceptionHandler;
import com.builderssas.api.domain.model.constructionrequest.dto.ConstructionRequestDto;
import com.builderssas.api.domain.model.constructionrequest.dto.CreateConstructionRequestDto;
import com.builderssas.api.repository.ConstructionTypeRepository;
import com.builderssas.api.repository.ProjectRepository;
import com.builderssas.api.repository.UserRepository;
import com.builderssas.api.services.constructionrequest.ConstructionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Controlador REST para gestionar solicitudes de construcción.
 *
 * NOTA:
 *  - Este controlador valida:
 *      • Existencia de proyecto
 *      • Existencia de tipo de construcción
 *      • Existencia de usuario
 *      • Presencia de coordenadas
 *  - La validación de ROLES (quién puede crear solicitudes)
 *    se moverá a la capa de servicio (ConstructionRequestServiceImpl),
 *    para respetar arquitectura y evitar duplicar reglas aquí.
 */

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/construction-requests")
public class ConstructionRequestController {

    private final ConstructionRequestService service;

    /* REQUERIDOS PARA VALIDACIÓN PREVIA */
    private final ProjectRepository projectRepository;
    private final ConstructionTypeRepository constructionTypeRepository;
    private final UserRepository userRepository;

    // ======================================================
    // VALIDACIONES FUNCIONALES — MÉTODOS PUROS
    // ======================================================

    /**
     * Verifica que el proyecto exista en BD, de forma funcional.
     */
    private Long ensureProjectExists(Long id) {
        return Optional.ofNullable(id)
                .filter(projectRepository::existsById)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException(
                                "El proyecto con id " + id + " no existe"));
    }

    /**
     * Verifica que el tipo de construcción exista en BD.
     */
    private Long ensureTypeExists(Long id) {
        return Optional.ofNullable(id)
                .filter(constructionTypeRepository::existsById)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException(
                                "El tipo de construcción con id " + id + " no existe"));
    }

    /**
     * Verifica que el usuario exista en BD.
     *
     * Aquí solo se valida existencia. La autorización fina por roles
     * se hará en la capa de servicio.
     */
    private Long ensureUserExists(Long id) {
        return Optional.ofNullable(id)
                .filter(userRepository::existsById)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.UnauthorizedException(
                                "El usuario con id " + id + " no existe o no tiene permisos"));
    }

    /**
     * Verifica que las coordenadas sean válidas usando programación funcional.
     * - dto no puede ser null
     * - latitude y longitude no pueden ser null
     */
    private CreateConstructionRequestDto ensureCoordinatesValid(CreateConstructionRequestDto dto) {
        return Optional.ofNullable(dto)
                .filter(d ->
                        Optional.ofNullable(d.getLatitude()).isPresent()
                                && Optional.ofNullable(d.getLongitude()).isPresent()
                )
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException(
                                "Latitude y longitude son obligatorios"));
    }

    // ======================================================
    // CREACIÓN DE SOLICITUD — FUNCIONAL + SIN LÓGICA DE ROLES
    // ======================================================

    /**
     * Crear solicitud de construcción.
     */
    @PostMapping
    public ResponseEntity<ConstructionRequestDto> create(
            @RequestBody CreateConstructionRequestDto dto,
            @RequestParam Long userId
    ) {

        // VALIDACIÓN PREVIA — coherencia de datos de entrada

        Optional.ofNullable(dto)
                .map(CreateConstructionRequestDto::getProjectId)
                .map(this::ensureProjectExists)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("projectId es obligatorio"));

        Optional.ofNullable(dto)
                .map(CreateConstructionRequestDto::getConstructionTypeId)
                .map(this::ensureTypeExists)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("constructionTypeId es obligatorio"));

        Optional.ofNullable(userId)
                .map(this::ensureUserExists)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.UnauthorizedException("userId es obligatorio"));

        Optional.ofNullable(dto)
                .map(this::ensureCoordinatesValid)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("Coordenadas inválidas"));

        // FLUJO ORIGINAL — delega al servicio de dominio
        return ResponseEntity.ok(service.create(dto, userId));
    }

    /**
     * Obtener solicitud por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConstructionRequestDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Listar todas las solicitudes.
     */
    @GetMapping
    public ResponseEntity<List<ConstructionRequestDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Listar solicitudes por proyecto.
     */
    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<List<ConstructionRequestDto>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.getByProject(projectId));
    }

    /**
     * Listar por estado.
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<ConstructionRequestDto>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                service.getByStatus(Enum.valueOf(
                        com.builderssas.api.domain.model.enums.RequestStatus.class, status
                ))
        );
    }
}
