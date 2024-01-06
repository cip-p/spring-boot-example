Feature: Feature to showcase cucumber testing style

  Background:
    Given There is no cake in the database


  Scenario: Create one cake
    When I create a cake with the following details
      | title       | chocolate cake             |
      | description | chocolate cake description |

    Then The HTTP response status code is 201
    And The following cake should exist in the database
      | title       | chocolate cake             |
      | description | chocolate cake description |


  Scenario: Create many cakes
    When I create the following cakes
      | title             | description             |
      | some title        | some description        |
      | another title     | another description     |
      | yet another title | yet another description |

    Then The following cakes should exist in the database
      | title             | description             |
      | some title        | some description        |
      | another title     | another description     |
      | yet another title | yet another description |
