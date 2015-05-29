Feature: Test how facilitator sign up works

Scenario: Registration of a new facilitator from a new device
    Given I have not signed up as a facilitator
    When I sign up as a facilitator from a new device
    Then I should be signed up as a facilitator

Scenario: Registration of a new facilitator from an existing device
    Given I have not signed up as a facilitator
    When I sign up as a facilitator from an existing device
    Then I should be signed up as a facilitator

Scenario: Registration of an existing facilitator from an existing device
    Given I have already signed up as a facilitator
    When I sign up as a facilitator from an existing device
    Then I should get facilitator already exist error

Scenario: Registration of an existing facilitator from a new device
    Given I have already signed up as a facilitator
    When I sign up as a facilitator from a new device
    Then I should get facilitator already exist error

Scenario: Registration of a new facilitator with already registered mobile number
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using an already registered mobile number
    Then I should get mobile number already exist error

Scenario: Registration of a new facilitator with blank name
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using blank name
    Then I should get name cannot be blank error

Scenario: Registration of a new facilitator with blank mobile number
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using blank mobile number
    Then I should get mobile number cannot be blank error

Scenario: Registration of a new facilitator with blank password
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using blank password
    Then I should get password cannot be blank error

Scenario: Registration of a new facilitator with blank requester id
    Given I have not signed up as a facilitator
    When I sign up as a facilitator with requester id as blank
    Then I should get invalid data error

Scenario: Registration of a new facilitator with password and confirm password being different
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using different password and confirm password
    Then I should get invalid password error

Scenario: Registration of a new facilitator with only numeric name
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using only numbers in the name field
    Then I should get invalid name error

Scenario: Registration of a new facilitator with alpha numeric name
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using alpha numeric characters in the name field
    Then I should get invalid name error

Scenario: Registration of a new facilitator with name containing special characters
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using special characters in the name field
    Then I should get invalid name error

Scenario: Registration of a new facilitator with name containing dot
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using alphabets and dot in the name field
    Then I should be signed up as a facilitator

Scenario: Registration of a new facilitator with name containing single alphabet
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using single alphabet in the name field
    Then I should get short name error

Scenario: Registration of a new facilitator with name containing two alphabets
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using two alphabets in the name field
    Then I should be signed up as a facilitator

Scenario: Registration of a new facilitator with less than 10 digits mobile number
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using less than 10 digits in the mobile number
    Then I should get an invalid mobile number error

Scenario: Registration of a new facilitator with more than 10 digits mobile number
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using more than 10 digits in the mobile number
    Then I should get an invalid mobile number error

Scenario: Registration of a new facilitator with non numeric mobile number
    Given I have not signed up as a facilitator
    When I sign up as a facilitator using non numeric characters in the mobile number
    Then I should get an invalid mobile number error
