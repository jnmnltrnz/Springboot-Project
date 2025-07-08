package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    @Query("SELECT a FROM Account a WHERE LOWER(a.username) = LOWER(:username)")
    Optional<Account> findByUsernameIgnoreCase(@Param("username") String username);
}