package com.company.team_management.repositories;

import com.company.team_management.entities.Programmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgrammerRepository extends JpaRepository<Programmer, Integer> {
    @Query("select p from Programmer p left join fetch p.projects")
    List<Programmer> findAllFetch();

    @Query("select p from Programmer p left join fetch p.projects where p.id = :id")
    Optional<Programmer> findByIdAndFetch(@Param("id") Integer id);

    @Query("select pg from Programmer pg left join fetch pg.projects pj left join fetch pj.tasks")
    List<Programmer> findAllFetchTask();
}
