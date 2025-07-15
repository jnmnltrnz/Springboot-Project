Feature: Login

  Scenario: Successful login with valid credentials
    Given a user exists with username "account" and password "123"
    When I enter "account" into the username field
    And I enter "123" into the password field
    And I click the login button
    Then I should be logged in successfully

  Scenario: Login fails with invalid credentials
    Given a user exists with username "account" and password "123"
    When I enter "wrong" into the username field
    And I enter "wrongpass" into the password field
    And I click the login button
    Then I should see an error message "Invalid username or password"
