package com.builderssas.api.repository;

import com.builderssas.api.domain.model.construction.ConstructionTypeMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstructionTypeMaterialRepository extends JpaRepository<ConstructionTypeMaterial, Long> {

    // ============================================================
    // Métodos existentes
    // ============================================================

    boolean existsByConstructionTypeIdAndMaterialTypeId(Long constructionTypeId, Long materialTypeId);


    // ============================================================
    // Nuevos métodos para cargar relaciones (JOIN FETCH)
    // ============================================================

    @Query("""
        SELECT ctm FROM ConstructionTypeMaterial ctm
        JOIN FETCH ctm.constructionType ct
        JOIN FETCH ctm.materialType mt
    """)
    List<ConstructionTypeMaterial> findAllWithRelations();

    @Query("""
        SELECT ctm FROM ConstructionTypeMaterial ctm
        JOIN FETCH ctm.constructionType ct
        JOIN FETCH ctm.materialType mt
        WHERE ctm.id = :id
    """)
    Optional<ConstructionTypeMaterial> findByIdWithRelations(Long id);

}
