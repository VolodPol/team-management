package com.company.team_management.repositories;

import com.company.team_management.entities.Programmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgrammerRepository extends JpaRepository<Programmer, Integer> {
    @Query("select p from Programmer p left join fetch p.projects")
    List<Programmer> findAllFetch();

    @Query("select p from Programmer p left join fetch p.projects where p.id = :id")
    Optional<Programmer> findByIdAndFetch(Integer id);
}
