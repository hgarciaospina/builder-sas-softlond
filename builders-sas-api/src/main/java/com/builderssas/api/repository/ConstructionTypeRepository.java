package com.builderssas.api.repository;

import com.builderssas.api.domain.model.construction.ConstructionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstructionTypeRepository extends JpaRepository<ConstructionType, Long> {

    // ============================================================
    // Métodos existentes
    // ============================================================

    boolean existsByNameIgnoreCase(String name);


    // ============================================================
    // Nuevos métodos para cargar relaciones
    // ============================================================

    @Query("SELECT ct FROM ConstructionType ct")
    List<ConstructionType> findAllWithRelations();

    @Query("SELECT ct FROM ConstructionType ct WHERE ct.id = :id")
    Optional<ConstructionType> findByIdWithRelations(Long id);


    // ============================================================
    // ✅ FIX: carga ConstructionType + materials + materialType
    //     para evitar LazyInitializationException
    // ============================================================

    @Query("""
        SELECT ct
        FROM ConstructionType ct
        LEFT JOIN FETCH ct.materials rel
        LEFT JOIN FETCH rel.materialType mt
        WHERE ct.id = :id
        """)
    Optional<ConstructionType> findByIdFetchMaterials(@Param("id") Long id);

}
