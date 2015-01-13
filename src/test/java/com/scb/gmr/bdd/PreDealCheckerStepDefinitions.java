package com.scb.gmr.bdd;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import CreditCheckAPI.CreditCheckException;
import CreditCheckAPI.CreditLimitBreach;

import com.scb.gmr.EventBus;
import com.scb.gmr.PreDealCheckerImpl;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class PreDealCheckerStepDefinitions {
    private long startTime = System.currentTimeMillis();

	private static PreDealCheckerImpl dealListener ;
	private String testCounterParty;
	
	private static Map<String, Integer> counterPartyTradeLimits = new HashMap<String, Integer>();
	private static Map<String, Integer> counterPartyDailyLimits = new HashMap<String, Integer>();

	public PreDealCheckerStepDefinitions() {
		System.out.println("Constructed PreDealCheckerStepDefinitions");
	}
	
	// stub out the legacy credit check as it was causing DB failures in test
	private static CreditLimitBreach creditCheck = new CreditLimitBreach() {
		@Override
		public void validate(String arg0, double arg1) throws CreditCheckException {
		}
	};
	
	// stub out an event bus
	private final EventBus eventBus = new EventBus() {
		@Override
		public void fire(String type, String reason) {
//			System.out.println("EVENT FIRING:"+ type +":reason="+reason);
		}
	};

	// Bit dodgy, but ok as tests are run serially... TODO:Find better way
    @Given("^a counterParty (.*)$")
    public void given_A_Counterparty(String counterParty) throws Throwable {
    	this.testCounterParty = counterParty;
    }

    @And("^pre authorised trading limit of (.*)$")
    public void and_A_PreAuthorised_Trading_Limit_Of(int preAuthorised) throws Throwable {
    	counterPartyTradeLimits.put(testCounterParty,  preAuthorised);
    }

    @And("^a daily trading limit of (.*)$")
    public void and_A_Daily_Trading_Limit_Of(int dailyLimit) throws Throwable {
    	counterPartyDailyLimits.put(testCounterParty,  dailyLimit);
    }
    
    private PreDealCheckerImpl createPreDealChecker() throws CreditCheckException {
		if (dealListener == null) {
			dealListener = new PreDealCheckerImpl(eventBus, creditCheck, counterPartyTradeLimits, counterPartyDailyLimits);
		} 
		
		return dealListener;
    }

    @When("^I place the order for (.*) with a (.*)$")
    public void when_I_Place_the_Following_Order(String counterParty, int notional) throws Throwable {
    	createPreDealChecker().handle(counterParty, notional);
    }
  
    @When("^I execute (.*) for (.*) with a (.*)$")
    public void when_I_Execute_the_Following_Order(int numberOfExecutions, final String counterParty, final int notional) throws Throwable {
    	int executionCount = 0;
//    	System.out.println(numberOfExecutions);
    	startTime = System.currentTimeMillis();
	
    	while (executionCount < numberOfExecutions) {
    		createPreDealChecker().handle(counterParty, notional);
        	executionCount++;
//        	System.out.println(executionCount);
    	}
    }


    @Then("^the trade should be successfully (.*)$")
    public void then_I_have_shared_at_hand(boolean expectedValue) throws Throwable {
    	Assert.assertTrue("Deal executed ok", createPreDealChecker().isLastTradeHasError());   	
    	Assert.assertEquals("Deal executed amount", expectedValue, createPreDealChecker().getLastTradeAmount());   	
    }

    @And("^the utilised daily limit should be (.*)$")
    public void and_The_Utilised_Daily_Limit_Should_Be(int utilisedDailyLimit) throws Throwable {
    	int amountUsedToday = createPreDealChecker().getCounterPartyDailyUsed().get(testCounterParty);
    	System.out.println("amountUsedToday: "+ amountUsedToday);
    	Assert.assertEquals("Daily limit used", utilisedDailyLimit, amountUsedToday);
    }

    @Then("^I should finish within (.*)$")
    public void then_I_Should_Finish_Within(int secondsToExecute) throws Throwable {
    	long runTime = System.currentTimeMillis() - startTime;
    	System.out.println("runTime(ms): "+ runTime);
    	Assert.assertTrue("executed fast enough", runTime/1000 < secondsToExecute);
    }


}



