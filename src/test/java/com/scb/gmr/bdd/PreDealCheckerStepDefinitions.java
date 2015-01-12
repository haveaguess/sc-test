package com.scb.gmr.bdd;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import CreditCheckAPI.CreditCheckException;
import CreditCheckAPI.CreditLimitBreach;

import com.scb.gmr.PreDealCheckerImpl;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class PreDealCheckerStepDefinitions {

	private PreDealCheckerImpl dealListener ;
	private String testCounterParty;
	private int testLimit;
	private int dailyLimit;
	Map<String, Integer> counterPartyDailyLimits ;
	
    @Given("^a counterParty (.*)$")
    public void given_A_Counterparty(String counterParty) throws Throwable {
    	this.testCounterParty = counterParty;
    }

    @And("^pre authorised trading limit of (.*)$")
    public void and_A_PreAuthorised_Trading_Limit_Of(int preAuthorised) throws Throwable {
    	this.testLimit = preAuthorised;
    }

    @And("^a daily trading limit of (.*)$")
    public void and_A_Daily_Trading_Limit_Of(int dailyLimit) throws Throwable {
    	this.dailyLimit = dailyLimit;
    	CreatePreDealChecker();
    }

    private Map<String, Integer> createLimitMap(String testCounterParty, int testLimit) {
    	Map<String, Integer> counterPartyLimits = new HashMap<String, Integer>();
    	System.out.println("adding "+testCounterParty+", "+testLimit);
    	counterPartyLimits.put(testCounterParty, testLimit);
    	return counterPartyLimits;
    }
    
    private void CreatePreDealChecker() throws CreditCheckException {
    	
    	// stub out the legacy credit check as it was causing DB failures in test
    	CreditLimitBreach creditCheck = new CreditLimitBreach() {
			@Override
			public void validate(String arg0, double arg1) throws CreditCheckException {
				// TODO Auto-generated method stub
			}
		};
		
		Map<String, Integer> counterPartyTradeLimits = createLimitMap(testCounterParty, testLimit);
		Map<String, Integer> counterPartyDailyLimits = createLimitMap(testCounterParty, dailyLimit);
		
    	dealListener = new PreDealCheckerImpl(creditCheck, counterPartyTradeLimits, counterPartyDailyLimits);
    }

    @When("^I place the order for (.*) with a (.*)$")
    public void when_I_Place_the_Following_Order(String counterParty, int notional) throws Throwable {
    	dealListener.handle(counterParty, notional);
    }

    long startTime = System.currentTimeMillis();
    
    @When("^I execute (.*) for (.*) with a (.*)$")
    public void when_I_Execute_the_Following_Order(int numberOfExecutions, final String counterParty, final int notional) throws Throwable {
    	int executionCount = 0;
//    	System.out.println(numberOfExecutions);
    	startTime = System.currentTimeMillis();
	
    	while (executionCount < numberOfExecutions) {
        	dealListener.handle(counterParty, notional);
        	executionCount++;
//        	System.out.println(executionCount);
    	}
    }


    @Then("^the trade should be successfully (.*)$")
    public void then_I_have_shared_at_hand(boolean expectedValue) throws Throwable {
    	Assert.assertTrue("Deal executed ok", dealListener.isFinishedWithoutError());   	
    	Assert.assertEquals("Deal executed amount", expectedValue, dealListener.getLastTrade());   	
    }

    @And("^the utilised daily limit should be (.*)$")
    public void and_The_Utilised_Daily_Limit_Should_Be(int utilisedDailyLimit) throws Throwable {
    	int amountRemainingToday = dealListener.getCounterPartyDailyRemaining().get(testCounterParty);
    	Assert.assertEquals("Daily limit used", utilisedDailyLimit, dailyLimit - amountRemainingToday);
    }

    @Then("^I should finish within (.*)$")
    public void then_I_Should_Finish_Within(int secondsToExecute) throws Throwable {
    	long runTime = System.currentTimeMillis() - startTime;
    	System.out.println("runTime(ms): "+ runTime);
    	Assert.assertTrue("executed fast enough", runTime/1000 < secondsToExecute);
    }


}



