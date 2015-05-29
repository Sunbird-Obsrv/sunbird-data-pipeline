Feature: test how location tagging of events work

Scenario: New user on a new device at a new location
    Given I have never played
    When I play for the first time on a new device
    Then my events should be tagged with my existing location

Scenario: New user on an existing device at an existing location
    Given I have never played
    When I play for the first time on an existing device from an existing location
    Then my events should be tagged with my existing location

Scenario: New used on an existing device moved to a new location
    Given I have never played
    When I play for the first time on an existing device from a new location
    Then my events should be tagged with my new location

Scenario: Existing user on a new device at an existing location
    Given I have already played
    When I play on an existing device from an existing location
    Then my new events should be tagged with my existing location

Scenario: Existing user on a new device at a new location
    Given I have already played
    When I play on a new device from a new location
    Then my new events should be tagged with my new location

Scenario: Existing user on an existing device at a new location
    Given I have already played
    When I play on an existing device from a new location
    Then my new events should be tagged with my new location

Scenario: Existing user on an existing device at an existing location
    Given I have already played
    When I play on an existing device from an existing location
    Then my new events should be tagged with my new location

Scenario: Multiple child plays at single location
    Given we have never played
    When we play on an existing device from a single location
    Then our events should be tagged with our existing location

Scenario: Multiple child plays at different locations
    Given we have already played
    When we play on an existing device from different locations
    Then our events should be tagged with our respective locations
