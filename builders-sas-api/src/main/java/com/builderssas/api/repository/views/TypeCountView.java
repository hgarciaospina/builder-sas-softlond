package com.builderssas.api.repository.views;

/**
 * Proyección para conteo de órdenes por tipo de construcción.
 * Usada tanto globalmente como por proyecto.
 */
public interface TypeCountView {
    Long getConstructionTypeId();
    String getConstructionTypeName();
    Long getTotal();
}