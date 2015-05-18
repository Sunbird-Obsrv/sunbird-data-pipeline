Feature: Test how signup location tagging works
  In order to check if signups are generated correctly
  As a developer
  I want it to behave in an expected way

  Scenario: Location unavailable
    Given I have never played
    When I play with GPS not available
    Then my signup should not be tagged to any location

  Scenario: Location later available
    Given I have never played
    When I play with GPS not available
    And I play with GPS available
    Then my signup should be tagged to my device location
