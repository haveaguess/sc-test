Feature: PreDealChecker

Scenario Outline: Validate Limits
Given a counterParty Ford
And  pre authorised trading limit of 500
And a daily trading limit of 1000
When I place the order for <counterParty> with a <tradeValue>
Then the utilised daily limit should be <utilisedDailyLimit>
Examples:
  | counterParty | tradeValue | utilisedDailyLimit |
  | Ford         | 501        |      501     |
  | Ford         | 5          |      506     |
  | Ford         | 409        |      915     |
  | Holden       | 409        |      915     |
  | Ford         | 200        |      915     |
  | Ford         | 85         |      1000    |
  | Ford         | 90         |      1000    |

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



