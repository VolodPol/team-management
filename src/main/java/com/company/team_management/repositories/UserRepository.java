package com.company.team_management.repositories;

import com.company.team_management.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select distinct u from User u join fetch u.tokens where u.email = ?1")
    Optional<User> findByEmail(String email);
}
