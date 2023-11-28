package com.company.team_management.repositories;

import com.company.team_management.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>, CustomProjectRepository {
    @Query("select p from Project p left join fetch p.programmers left join fetch p.tasks")
    List<Project> findAllFetch();

    @Query("select p from Project p left join fetch p.programmers where p.id = :id")
    Optional<Project> findByIdFetch(Integer id);
}
