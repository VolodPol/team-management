package com.company.team_management.repositories;

import com.company.team_management.entities.Project;

import java.util.List;

public interface CustomProjectRepository {
    List<Project> getAllFetchAllWithinBudget(Long lowerBound, Long upperBound);
}
