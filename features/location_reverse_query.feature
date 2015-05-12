Feature: Test how location tagging works

  Scenario: Simple search
    Given I have never played
    When I play from a location
    Then lat long should be reverse searched
    And events should be tagged to respective locations
    And my device should also be tagged to that location

  Scenario: Same location in the same day
    Given I have never played
    When I play from a location
    When I play from another location
    When I play from another location
    Then lat long should be reverse searched
    And events should be tagged to respective locations

  Scenario: Dysfunctional GPS
    Given I have never played
    When I play from different locations with defective GPS reading
    Then lat long should be reverse searched
    And events should be tagged to respective locations
    And missing GPS events should be tagged to the devices current location
