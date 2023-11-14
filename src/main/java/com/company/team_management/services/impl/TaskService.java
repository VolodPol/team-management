package com.company.team_management.services.impl;

import com.company.team_management.entities.Task;
import com.company.team_management.exceptions.already_exists.TaskAlreadyExistsException;
import com.company.team_management.exceptions.no_such.NoSuchTaskException;
import com.company.team_management.repositories.TaskRepository;
import com.company.team_management.services.IService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TaskService implements IService<Task> {
    private final TaskRepository repository;
    @CacheEvict(cacheNames = "tasks", allEntries = true)
    @Transactional
    @Override
    public Task save(Task task) {
        Integer id = task.getId();
        if (id != null && repository.findById(id).orElse(null) != null) {
            throw new TaskAlreadyExistsException("Task already exists!");
        }
        return repository.save(task);
    }

    @Cacheable("tasks")
    @Transactional(readOnly = true)
    @Override
    public List<Task> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Task findById(int id) {
        return findIfPresent(id, repository::findById);
    }

    @CacheEvict(cacheNames = "tasks", allEntries = true)
    @Transactional
    @Override
    public void deleteById(int id) {
        findIfPresent(id, repository::findById);
        repository.deleteById(id);
    }

    @CacheEvict(cacheNames = "tasks", allEntries = true)
    @Transactional
    @Override
    public Task updateById(int id, Task task) {
        Task found = findIfPresent(id, repository::findById);

        setNullable(found::setName, task.getName());
        setNullable(found::setStatus, task.getStatus());
        return found;
    }

    private Task findIfPresent(int id, Function<Integer, Optional<Task>> finder) {
        Optional<Task> task = finder.apply(id);
        if (task.isEmpty())
            throw new NoSuchTaskException(
                    String.format("There is no employee with id = %d", id)
            );
        return task.get();
    }
}
