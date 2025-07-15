package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.Account;
import net.javaguides.springboot_backend.payload.ApiResponse;
import net.javaguides.springboot_backend.payload.LogoutRequest;
import net.javaguides.springboot_backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<Account>>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Account>> login(@RequestBody Account loginRequest) {
        Account account = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(ApiResponse.success("Login successful", account));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody LogoutRequest logoutRequest) {
        String result = accountService.logout(logoutRequest);
        return ResponseEntity.ok(ApiResponse.success(result, null));
    }
}