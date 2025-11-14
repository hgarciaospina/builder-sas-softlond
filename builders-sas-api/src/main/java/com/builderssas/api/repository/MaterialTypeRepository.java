package com.builderssas.api.repository;

import com.builderssas.api.domain.model.material.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialTypeRepository extends JpaRepository<MaterialType, Long> {

    // ============================================================
    // Métodos existentes (se mantienen sin modificaciones)
    // ============================================================

    Optional<MaterialType> findByCode(String code);

    boolean existsByCode(String code);


    // ============================================================
    // Nuevos métodos para cargar relaciones (JOIN FETCH)
    // No afectan servicios existentes
    // ============================================================

    @Query("SELECT m FROM MaterialType m")
    List<MaterialType> findAllWithRelations();

    @Query("SELECT m FROM MaterialType m WHERE m.id = :id")
    Optional<MaterialType> findByIdWithRelations(Long id);

}
