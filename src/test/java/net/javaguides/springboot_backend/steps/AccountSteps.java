package net.javaguides.springboot_backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import net.javaguides.springboot_backend.SpringbootBackendApplication;
import net.javaguides.springboot_backend.controller.AccountController;
import net.javaguides.springboot_backend.entity.Account;
import net.javaguides.springboot_backend.payload.ApiResponse;
import net.javaguides.springboot_backend.repositories.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@CucumberContextConfiguration
@SpringBootTest(classes = SpringbootBackendApplication.class)
public class AccountSteps {

    @Autowired
    private AccountController accountController;

    @Autowired
    private AccountRepository accountRepository;

    private Account loginRequest;
    private ResponseEntity<ApiResponse<Account>> loginResponse;
    private Exception exception;

    @Before
    public void clearDatabase() {
        accountRepository.deleteAll();
    }

    @Given("a user exists with username {string} and password {string}")
    public void a_user_exists_with_credentials(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setAuthenticated(false);
        account.setSessionId(null);
        account.setLastLogin(LocalDateTime.now());
        accountRepository.save(account);

        loginRequest = new Account();  // Initialize login request object
    }

    @When("I enter {string} into the username field")
    public void i_enter_username(String username) {
        loginRequest.setUsername(username);
    }

    @And("I enter {string} into the password field")
    public void i_enter_password(String password) {
        loginRequest.setPassword(password);
    }

    @And("I click the login button")
    public void i_click_the_login_button() {
        try {
            loginResponse = accountController.login(loginRequest);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("I should be logged in successfully")
    public void i_should_be_logged_in_successfully() {
        Assertions.assertNotNull(loginResponse, "Login response should not be null");
        Assertions.assertTrue(loginResponse.getBody().isSuccess(), "API response should be successful");
        
        Account account = loginResponse.getBody().getData();
        Assertions.assertNotNull(account, "Account data should not be null");
        Assertions.assertTrue(account.isAuthenticated(), "User should be authenticated");
        Assertions.assertNotNull(account.getSessionId(), "Session ID should not be null");
    }

    @Then("I should see an error message {string}")
    public void i_should_see_an_error_message(String expectedMessage) {
        Assertions.assertNotNull(exception, "An exception should have occurred");
        Assertions.assertTrue(exception.getMessage().contains(expectedMessage),
                "Expected error message to contain: " + expectedMessage);
    }
}
