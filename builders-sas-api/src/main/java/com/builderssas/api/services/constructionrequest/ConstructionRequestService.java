package com.builderssas.api.services.constructionrequest;

import com.builderssas.api.domain.model.constructionrequest.dto.ConstructionRequestDto;
import com.builderssas.api.domain.model.constructionrequest.dto.CreateConstructionRequestDto;
import com.builderssas.api.domain.model.enums.RequestStatus;

import java.util.List;

/**
 * Servicio para la gestión de solicitudes de construcción.
 */
public interface ConstructionRequestService {

    /** Crear una nueva solicitud. */
    ConstructionRequestDto create(CreateConstructionRequestDto dto, Long userId);

    /** Obtener solicitud por ID. */
    ConstructionRequestDto getById(Long id);

    /** Listar todas las solicitudes. */
    List<ConstructionRequestDto> getAll();

    /** Listar solicitudes por proyecto. */
    List<ConstructionRequestDto> getByProject(Long projectId);

    /** Listar solicitudes por estado. */
    List<ConstructionRequestDto> getByStatus(RequestStatus status);
}
