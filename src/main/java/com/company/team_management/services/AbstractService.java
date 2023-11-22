package com.company.team_management.services;

import com.company.team_management.exceptions.no_such.NoSuchEntityException;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractService<E> implements IService<E> {

    protected <T> void setNullable(Consumer<T> setter, T value) {
        if (value != null)
            setter.accept(value);
    }

    protected E findIfPresent(int id, Function<Integer, Optional<E>> finder) {
        return finder
                .apply(id)
                .orElseThrow(
                        () -> new NoSuchEntityException(String.format("There is no entity with id = %d", id))
                );
    }
}
