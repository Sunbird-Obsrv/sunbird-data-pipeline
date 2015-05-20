Feature: Test how device count works

Scenario: New device used by one child
    Given the device has never been used
    When I play for the first time on this device
    Then a new device will be added
