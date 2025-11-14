package com.builderssas.api.services.constructionorder;

import com.builderssas.api.domain.model.construction.ConstructionOrder;
import com.builderssas.api.domain.model.construction.ConstructionRequest;

import java.util.concurrent.CompletableFuture;

/**
 * Servicio para crear órdenes desde solicitudes aprobadas.
 */
public interface ConstructionOrderService {

    /**
     * Crea una orden de construcción a partir de una solicitud APROBADA.
     * Debe ejecutar validaciones, fechas, stock, observaciones y estado.
     */
    CompletableFuture<ConstructionOrder> createOrderFromRequest(ConstructionRequest request);

}
