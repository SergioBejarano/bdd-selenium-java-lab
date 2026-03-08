Feature: Geolocation on The Internet

  Scenario: See the geolocation button on the page
    Given I am on The Internet geolocation page
    Then I should see the button "Where am I?"

  Scenario: Retrieve latitude and longitude coordinates
    Given I am on The Internet geolocation page
    When I click "Where am I?"
    Then I should see the latitude on screen
    And I should see the longitude on screen
