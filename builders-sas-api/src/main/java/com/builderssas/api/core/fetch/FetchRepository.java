package com.builderssas.api.core.fetch;

import java.util.List;
import java.util.Optional;

public interface FetchRepository<T> {

    List<T> findAllWithRelations();

    Optional<T> findByIdWithRelations(Long id);
}
