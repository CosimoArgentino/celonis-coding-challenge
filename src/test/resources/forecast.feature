Feature: save city forecast
  Scenario: Do today the request and do the same request the next day
    Given today is June 7.
    When I request saving the forecast for Badajoz

    Then the system will save the forecast for Badajoz for June 7 and 8 after fetching that data from the Weather API.
    When I request saving the forecast for Badajoz the next day (June 8).
    Then The system will skip saving the forecast for June 8 (since it was saved yesterday) and will only save the forecast for June 9.


Feature: fetch all forecasts
  Scenario: three city available on the system
    Given that:Today is June 7. I already have saved the forecast for today and tomorrows for Badajoz. I have also saved the forecast for today and tomorrows for Zaragoza. And for London, we only have today's forecast.
    When I fetch all available forecasts. Then I receive a sorted list including: Badajoz with today's and tomorrow's forecast London with today's forecast Zaragoza with today's and tomorrow's forecast
    Given that: Today June 9. No forecasts are available in the system.
    When I fetch all the existing forecasts
    Then I receive no data (an empty payload) from the API