package com.builderssas.api.domain.model.construction.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * DTO representing a construction type for read operations.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionTypeDto {

    @NotNull(message = "El ID del tipo de construcción no puede ser nulo.")
    private Long id;

    @NotBlank(message = "El nombre del tipo de construcción es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String name;

    @NotNull(message = "La duración en días es obligatoria.")
    @Positive(message = "La duración debe ser un número positivo.")
    private Integer durationDays;

    @NotNull(message = "La fecha de creación no puede ser nula.")
    private OffsetDateTime createdAt;
}
