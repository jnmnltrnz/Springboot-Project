package net.javaguides.springboot_backend.service;

import net.javaguides.springboot_backend.entity.Account;
import net.javaguides.springboot_backend.exception.AuthenticationException;
import net.javaguides.springboot_backend.exception.ResourceNotFoundException;
import net.javaguides.springboot_backend.payload.LogoutRequest;
import net.javaguides.springboot_backend.repositories.AccountRepository;
import net.javaguides.springboot_backend.utils.RandomAlpaNumUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

   @Autowired
    private RandomAlpaNumUtils rAlpaNumUtils;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountUser(Long id) {
        return accountRepository.findById(id);
    }

    public Account updatePassword(Long id, String newPassword) {

        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));

        existing.setPassword(newPassword);
        existing.setDefaultPassword(false);

        return accountRepository.save(existing);
    }

    public Account resetUserAccount(Long id, String username) {

        Account isAdmin = accountRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin account not found: " + username));

        /// requestor, only admin can allow to reset
        if (isAdmin.getUsername() == username) {
            Account accountToReset = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account user to reset not found: " + id));

            String rawPassword = rAlpaNumUtils.generateRandomAlphanumeric(10);

            accountToReset.setDefaultPassword(true);
            accountToReset.setPassword(rawPassword);

            return accountRepository.save(accountToReset);
        }

        throw new AuthenticationException("Reset password not allowed.");
      
    }

    public Account login(String username, String password) {
        String trimmedUsername = username.trim();
        String trimmedPassword = password.trim();

        Optional<Account> accountOpt = accountRepository.findByUsername(trimmedUsername);

        if (accountOpt.isPresent() && accountOpt.get().getPassword().equals(trimmedPassword)) {
            Account account = accountOpt.get();
            account.setSessionId(UUID.randomUUID().toString());
            account.setLastLogin(LocalDateTime.now());
            account.setAuthenticated(true);
            return accountRepository.save(account);
        }

        throw new AuthenticationException("Invalid username or password");
    }

    public String logout(LogoutRequest logoutRequest) {
        Optional<Account> accountOpt = accountRepository.findByUsername(logoutRequest.getUsername());

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setAuthenticated(false);
            account.setSessionId(null);
            accountRepository.save(account);
            return "Logged out successfully";
        }

        throw new ResourceNotFoundException("Account not found");
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with username: " + username));
    }

    public boolean isAuthenticated(String username) {
        return accountRepository.findByUsername(username)
                .map(Account::isAuthenticated)
                .orElse(false);
    }
}