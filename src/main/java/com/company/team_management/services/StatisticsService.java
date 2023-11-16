package com.company.team_management.services;

import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.repositories.ProgrammerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final ProgrammerRepository repository;

    @Cacheable(value = "bestProgrammers")
    public List<Programmer> findMostSuccessful() {
        return repository.findAllFetchTask().stream()
                .filter(hasFinishedTasks())
                .sorted(Comparator.comparing(numOfDoneTasksFunction()).reversed())
                .collect(Collectors.toList());
    }

    private Predicate<Programmer> hasFinishedTasks() {
        return programmer -> programmer.getProjects().stream()
                .map(Project::getTasks)
                .flatMap(Collection::stream)
                .anyMatch(task -> task.getStatus().equals(Task.Status.FINISHED));
    }

    private Function<Programmer, Long> numOfDoneTasksFunction() {
        return programmer -> programmer.getProjects().stream()
                .mapToLong(value -> value.getTasks().stream()
                        .filter(task -> task.getStatus().equals(Task.Status.FINISHED))
                        .count())
                .sum();
    }
}
