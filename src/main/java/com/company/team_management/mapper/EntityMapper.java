package com.company.team_management.mapper;

import java.util.List;

public interface EntityMapper<E, D> {
    D entityToDTO(E entity);
    E dtoToEntity(D dto);

    List<D> collectionToDTO(List<E> entities);
    List<E> collectionFromDTO(List<D> dtoList);
}
