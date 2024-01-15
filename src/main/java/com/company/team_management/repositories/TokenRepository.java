package com.company.team_management.repositories;

import com.company.team_management.entities.users.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
    select t from Token t inner join User u on t.user.id = u.id
    where u.id = ?1 and (t.expired = false and t.revoked = false)
    """)
    List<Token> findTokensByUserId(Integer id);
    @Query("select t from Token t where t.token = :token")
    Optional<Token> findByToken(@Param("token") String token);
}
