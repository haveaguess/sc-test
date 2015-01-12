Pre Deal Checker Exercise Task Description
========================================

Implement a Pre Deal Checker Agent
------------------------------

The business require an PreDealChecker agent that will autonomously validate trades against predefined trade limits and daily trading limits
for a specified counter party.

* The PreDealChecker needs to be able to validate a given trade for a given counter party against given trading limits and daily limits.
 *  Trades which are below the trading limit threshold don't need to be posted to the Legacy CreditCheck system
 *  Trades which are above the trading limit threshold will need to be posted against the Legacy CreditCheck system
 *  Once the Daily Limit is reached, no more trades can be placed. Partial trades cannot be executed however in order to satisfy the business
    we must utilise as much of the limit as possible.

* The agent must implement the PreDealChecker interface.
* The agent should signal the Finished event when it has no more work (reached the daily limit) to do and signal the Error event whenever an error occurs.

** Asynchronous and Multi threaded **

Because the trading framework is asynchronous and multi threaded, the PreDealChecker will not be removed immediately after firing the Finished event; it might still
receive some trades before being disposed. The PreDealChecker agent receives trades by implementing the PreDealListener interface.

When deployed, the trading framework will route trades to the PreDealChecker.

The business have the requirement that we should be able to show the throughput of the system and have included a sample Cucumber file
with the appropriate step definitions to help you get started.

** CreditCheckAPI **

Where the Trade is above the daily limits, the agent must post the trade to the Legacy CreditCheck system for the Credit Risk Officers to monitor the following day.
There's a API for talking to the legacy system, the CreditLimitBreach interface and the CreditCheck class that implements that interface.
The methods on CreditLimitBreach do not return a status code. The CreditCheck object actually sends reliable, asynchronous messages to the Legacy CreditCheck system.
A successful call to validate means that the message will be delivered to the legacy system. The CreditCheck object throws a CreditCheckException if the
message cannot be sent.


