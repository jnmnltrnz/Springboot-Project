package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByUsername(String username);

    @Query("SELECT a FROM Account a WHERE LOWER(a.username) = LOWER(:username)")
    Optional<Account> findByUsernameIgnoreCase(@Param("username") String username);
    
    List<Account> findByAuthenticatedTrue();
    
    List<Account> findByAuthenticatedFalse();
    
    @Query("SELECT a FROM Account a WHERE a.lastLogin >= :since")
    List<Account> findActiveAccountsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.authenticated = true")
    long countAuthenticatedAccounts();
    
    boolean existsByUsername(String username);
}