Feature: Test how unique signup works
  In order to check if signups are generated correctly
  As a developer
  I want it to behave in an expected way

  Scenario: Single Session
    Given I have never played
    When I play for the first time
    Then I should see a unique signup

  Scenario: Multiple Sessions on the same day
    Given I have never played
    When I play multiple times on the same day
    Then I should see a unique signup

  Scenario: Multiple Sessions on different days
    Given I have never played
    When I play multiple times on multiple days
    Then I should see a unique signup
