Feature: PreDealChecker


Scenario Outline: Non Functional Performance for the Pre Deal Checker
Given a counterParty Ford
And  pre authorised trading limit of 5000
And a daily trading limit of 100000
When I execute <numberOfTrades> for <counterParty> with a <tradeValue>
Then I should finish within <seconds>
Examples:
  | counterParty | tradeValue | validated | numberOfTrades | seconds |
  | Ford         | 50         |   true    |    25          |  10     |
  | Holden       | 50         |   true    |    50000       |  1      |
  | Ford         | 50         |   true    |    100000      |  60     |



