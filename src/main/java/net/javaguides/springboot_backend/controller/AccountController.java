package net.javaguides.springboot_backend.controller;

import jakarta.transaction.Transactional;
import net.javaguides.springboot_backend.entity.Account;
import net.javaguides.springboot_backend.payload.LogoutRequest;
import net.javaguides.springboot_backend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    @PostMapping("/login")
    public Account login(@RequestBody Account loginRequest) {
        String username = loginRequest.getUsername().trim();
        String password = loginRequest.getPassword().trim();

        Optional<Account> accOpt = accountRepository.findByUsername(username);

        if (accOpt.isPresent() && accOpt.get().getPassword().equals(password)) {
            Account account = accOpt.get();
            account.setSessionId(UUID.randomUUID().toString());
            account.setLastLogin(LocalDateTime.now());
            account.setAuthenticated(true);
            accountRepository.save(account);
            return account;
        }

        throw new RuntimeException("Invalid username or password");
    }


    @Transactional
    @PostMapping("/logout")
    public String logout(@RequestBody LogoutRequest logoutRequest) {
        System.out.println("Logout called for: " + logoutRequest.getUsername());

        Optional<Account> accOpt = accountRepository.findByUsername(logoutRequest.getUsername());
        if (accOpt.isPresent()) {
            Account account = accOpt.get();
            account.setAuthenticated(false);
            account.setSessionId(null);

            accountRepository.save(account);

            System.out.println("Updated account: " + account);
            return "Logged out";
        }
        throw new RuntimeException("Account not found");
    }

}