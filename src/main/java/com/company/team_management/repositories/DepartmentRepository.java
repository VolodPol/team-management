package com.company.team_management.repositories;

import com.company.team_management.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("select d from Department d left join fetch d.programmers")
    List<Department> findAllFetch();

    @Query("select d from Department d left join fetch d.programmers where d.id = :id")
    Optional<Department> findByIdFetch(int id);
}
