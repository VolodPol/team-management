package com.company.team_management.services;

import java.util.List;

public interface IService<E> extends Settable {
    E save(E entity);
    List<E> findAll();
    E findById(int id);
    void deleteById(int id);
    E updateById(int id, E entity);
}
