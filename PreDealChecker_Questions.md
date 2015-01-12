Some questions
========================================

Assumptions
------------------------------


 *  Trades which are below the trading limit threshold don't need to be posted to the Legacy CreditCheck system
 *  Trades which are above the trading limit threshold will need to be posted against the Legacy CreditCheck system



**It's not clear how we are expected to handle the boundary condition. ie. what if the trade doesnt bring total over or under the limit but exactly matches it ? I assume this is not an exception **  



Because the trading framework is asynchronous and multi threaded

**It's not clear if this method needs to be thread safe, for safety I could make syncronized, and we could revisit if performance is bad. Im assuming thread-safety not part of requirements **  




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

Should create a test for it's failure as well, or this informal failure is enough ? 

Since we are setting daily limits when is the day over ? 

Can't really fire Finish unless we have definitely used up ALL the daily limit, otherwise there may be a trade that could arrive that could use the last little bit. or just when used up for all counterparties?

Need to raise error for daily limits ? 
