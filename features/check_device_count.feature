Feature: Test how device count works

Scenario: New device used by one child
    Given the device has never been used
    When I play on this device
    Then a new device will be added

Scenario: Existing device
    Given the device has already been used
    When I play on this device on the same day
    Then the number of devices does not change

Scenario: Existing device moves to a different location
    Given the device has already been used
    When I play on this device from a different location
    Then the number of devices does not change

Scenario: Existing device is used across days
    Given the device has already been used
    When I play on this device on a different day
    Then the number of devices does not change

Scenario: Existing device is used across days and locations
    Given the device has already been used
    When I play on this device on a different day at a different location
    Then the number of devices does not change

Scenario: Existing device is used across days location and children
    Given the device has already been used
    When all of us play on this device on different days and at different locations
    Then the number of devices does not change

Scenario: Existing device used by existing and new children
    Given the device has already been used
    When all of us including new children play on this device
    Then the number of devices does not change
