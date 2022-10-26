@Lessons
Feature: Lessons
  As a user
  I want to interact with lessons
  So that I might know what I need to learn

  Scenario Outline: Create a new lesson
    Given I am an authenticated user
    And I have a lesson
    And The lesson has course with <number> characters
    And The lesson has unit with <number> characters
    When I submit the lesson
    Then I should receive a success response

    Examples:
      | number |
      | 255    |
      | 1      |
      | 25     |
      | 52     |
      | 250    |
      | 205    |

  Scenario Outline: Create a lesson without required data
    Given I am an authenticated user
    And I have a lesson
    And The lesson has course with <number> characters
    When I submit the lesson
    Then I should receive an InvalidPayloadException

    Examples:
      | number |
      | 0      |
      | 256    |

  Scenario: Get a lesson
    Given I am an authenticated user
    And A lesson exists
    When I get the lesson
    Then I should receive a success response
    And A lesson should be received

  Scenario: Update an existing lesson
    Given I am an authenticated user
    And A lesson exists
    And The lesson has course with 50 characters
    And The lesson has unit with 45 characters
    When I submit the lesson for update
    Then I should receive a success response

  Scenario: Delete a lesson
    Given I am an authenticated user
    And A lesson exists
    When I delete the lesson
    Then I should receive a success response
    And The lesson should be removed

  Scenario Outline: Create a lesson as an unauthenticated user
    Given I have a lesson
    And The lesson has course with <number> characters
    And The lesson has unit with <number> characters
    When I submit the lesson
    Then I should receive an unauthenticated response

    Examples:
      | number |
      | 15     |

  Scenario: Get a lesson as an unauthenticated user
    Given A lesson exists
    When I get the lesson
    Then I should receive an unauthenticated response

  Scenario: Update an existing lesson as an unauthenticated user
    Given A lesson exists
    And The lesson has course with 50 characters
    And The lesson has unit with 45 characters
    When I submit the lesson for update
    Then I should receive an unauthenticated response

  Scenario: Delete a lesson as an unauthenticated user
    Given A lesson exists
    When I delete the lesson
    Then I should receive an unauthenticated response

  Scenario: Get all attended lessons as an unauthenticated user
    Given A lesson exists
    When I get all attended lessons
    Then I should receive an unauthenticated response

  Scenario: Get all attended lessons
    Given I am an authenticated user
    And A lesson exists
    When I get all attended lessons
    Then I should receive a success response

  Scenario: Get all lessons for a given course as an unauthenticated user
    Given A lesson exists
    When I get all lessons for a given course
    Then I should receive an unauthenticated response

  Scenario: Get all lessons for a given course
    Given I am an authenticated user
    And A lesson exists
    When I get all lessons for a given course
    Then I should receive a success response

  Scenario: Get a list of all courses as an unauthenticated user
    Given A lesson exists
    When I get a list of all courses
    Then I should receive an unauthenticated response

  Scenario: Get a list of all courses
    Given I am an authenticated user
    And A lesson exists
    When I get a list of all courses
    Then I should receive a success response

  Scenario: Get all lessons as an unauthenticated user
    Given A lesson exists
    When I get all lessons
    Then I should receive an unauthenticated response

  Scenario: Get all lessons
    Given I am an authenticated user
    And A lesson exists
    When I get all lessons
    Then I should receive a success response
