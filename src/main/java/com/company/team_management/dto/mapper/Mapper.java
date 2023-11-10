package com.company.team_management.dto.mapper;

import java.util.Collection;
import java.util.List;

public abstract class Mapper<E, D> {
    public abstract D toDto(E entity);
    public abstract E fromDto(D dto);

    public List<D> collectionToDto(Collection<E> entities) {
        return entities
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<E> collectionFromDto(Collection<D> DTOs) {
        return DTOs
                .stream()
                .map(this::fromDto)
                .toList();
    }
}
