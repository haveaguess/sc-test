General Assumptions
===================
 
Im assuming :

 *  We are just concerned with unit tests
 *  No need for a database interface (and hence fault tolerance/outages concerns)
 *  No need to consider when end of day is or handling of resetting limits (perhaps server is just restarted!)
 
 
Some questions
========================================

>  *  Trades which are below the trading limit threshold don't need to be posted to the Legacy CreditCheck system
>  *  Trades which are above the trading limit threshold will need to be posted against the Legacy CreditCheck system

**It's not clear how we are expected to handle the boundary condition. ie. what if the trade doesnt bring total over or under the limit but exactly matches it ? Could disambiguate**  


> "Because the trading framework is asynchronous and multi threaded"

**It's not clear if this method needs to be thread safe, for safety I could make it synchronized, and we could revisit if performance is bad. Im assuming thread-safety is not part of requirements **  



DAILY LIMITS CONFUSION?!
======

It's occured to me that daily limits may not even be per - counterparty as I first read the spec : 

> "..validate a given trade for a given counter party against given trading limits and daily limits." 

This long sentence actually has TWO possible interpretations (and assuming awareness of "pre-auth" concept hinted at in spec to relate to the legacy credit check system): 

 *  Daily and pre-auth trading limits are per counterparty, for each trade
 *  Pre-auth Trading limits are per counterparty, Daily limits are are a cap on total daily trading.

I programmed it as the latter (harder!) but can readily rollback

The test cases would pass either way from what I can tell by observation. Because they only test the unspecified limits for "Holden" but not for mixed counterparty/daily limits. They should be enlarged to cover more test cases to remove ambiguity.

If we have different counterparties with different daily limits the only way we can fire "Finished" is if we use up all the daily limits in all the counterparties. If its just a global, again that's easier too..



Implementation notes
------------------------------

Initial implementation doesnt work because the 

    [junit] CreditCheckAPI.CreditCheckException: Could not connect to Database
    [junit]     at CreditCheckAPI.CreditCheck.<init>(CreditCheck.java:6)
    [junit]     at com.scb.gmr.PreDealCheckerImpl.<init>(Unknown Source)
    [junit]     at com.scb.gmr.bdd.PreDealCheckerStepDefinitions.given_A_Counterparty(Unknown Source)
    [junit]     at ?.Given a counterParty Ford(PreDealChecker.feature:4)
    [junit]


So we will need to mock it out for tests. 

But Readme says "You can only use mocking on the PreDealListener Interface" so how can I mock out the bad connection to the database!?! Just using a stub implementation for now


