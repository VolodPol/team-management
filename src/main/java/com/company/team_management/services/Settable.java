package com.company.team_management.services;

import java.util.function.Consumer;

public interface Settable {
    default <T> void setNullable(Consumer<T> setter, T value) {
        if (value != null)
            setter.accept(value);
    }
}
