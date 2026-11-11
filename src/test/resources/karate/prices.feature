Feature: E-Commerce Product Price Retrieval API

  Background:
    * url baseUrl

  Scenario Outline: Query applicable product price for official test cases
    Given path '/api/v1/prices'
    And param brandId = <brandId>
    And param productId = <productId>
    And param applicationDate = '<applicationDate>'
    When method GET
    Then status 200
    And match response.brandId == <brandId>
    And match response.productId == <productId>
    And match response.priceList == <expectedPriceList>
    And match response.price == <expectedPrice>
    And match response.currency == '<expectedCurrency>'
    And match response.startDate == '<expectedStartDate>'
    And match response.endDate == '<expectedEndDate>'

    Examples:
      | brandId | productId | applicationDate     | expectedPriceList | expectedPrice | expectedCurrency | expectedStartDate   | expectedEndDate     |
      | 1       | 35455     | 2020-06-14T10:00:00 | 1                 | 35.50         | EUR              | 2020-06-14T00:00:00 | 2020-12-31T23:59:59 |
      | 1       | 35455     | 2020-06-14T16:00:00 | 2                 | 25.45         | EUR              | 2020-06-14T15:00:00 | 2020-06-14T18:30:00 |
      | 1       | 35455     | 2020-06-14T21:00:00 | 1                 | 35.50         | EUR              | 2020-06-14T00:00:00 | 2020-12-31T23:59:59 |
      | 1       | 35455     | 2020-06-15T10:00:00 | 3                 | 30.50         | EUR              | 2020-06-15T00:00:00 | 2020-06-15T11:00:00 |
      | 1       | 35455     | 2020-06-16T21:00:00 | 4                 | 38.95         | EUR              | 2020-06-15T16:00:00 | 2020-12-31T23:59:59 |

  Scenario: Return 404 ProblemDetail when no price exists for given product
    Given path '/api/v1/prices'
    And param brandId = 1
    And param productId = 99999
    And param applicationDate = '2020-06-14T10:00:00'
    When method GET
    Then status 404
    And match response.status == 404
    And match response.title == 'Price Not Found'
    And match response.instance == '/api/v1/prices'

  Scenario: Return 400 ProblemDetail when required query parameter is missing
    Given path '/api/v1/prices'
    And param brandId = 1
    And param applicationDate = '2020-06-14T10:00:00'
    When method GET
    Then status 400
    And match response.status == 400
