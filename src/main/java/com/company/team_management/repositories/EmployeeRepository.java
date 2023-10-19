package com.company.team_management.repositories;

import com.company.team_management.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("select e from Employee e left join fetch e.projects projects")
    List<Employee> findAllFetch();

    @Query("select e from Employee e left join fetch e.projects where e.id = :id")
    Optional<Employee> findByIdAndFetch(Integer id);
}
