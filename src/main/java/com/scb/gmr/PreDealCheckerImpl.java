package com.scb.gmr;

import java.util.HashMap;
import java.util.Map;

import CreditCheckAPI.CreditCheckException;
import CreditCheckAPI.CreditLimitBreach;
import CreditCheckAPI.PreDealListener;

// signal the Finished event when it has no more work (reached the daily limit)  
// signal the Error event whenever an error occurs.
public class PreDealCheckerImpl implements PreDealListener {

	private CreditLimitBreach legacyCreditSystem;
	
	private Map<String, Integer> counterPartyTradeLimits;
	// keep a copy of original limits for reference (unused)
	private Map<String, Integer> counterPartyDailyLimits;
	
	private Map<String, Integer> counterPartyDailyRemaining;
	
	public Map<String, Integer> getCounterPartyDailyRemaining() {
		return counterPartyDailyRemaining;
	}


	// trade history
	private int lastTrade = -1;


	// execution history 
	private boolean finishedWithoutError = false; 
	

	/**
	 * We can't construct without legacy system
	 * @throws CreditCheckException when legacy systems fails 
	 */
	public PreDealCheckerImpl(CreditLimitBreach legacyCreditSystem,
			Map<String, Integer> counterPartyTradeLimits,
			Map<String, Integer> counterPartyDailyLimits) {
		this.legacyCreditSystem  = legacyCreditSystem;
		this.counterPartyDailyLimits = counterPartyDailyLimits; 		 
		this.counterPartyTradeLimits = counterPartyTradeLimits; 	
		counterPartyDailyRemaining = new HashMap<String, Integer>(counterPartyDailyLimits);
	}
	
	// after firing the Finished event;  might still receive some trades
	// before being disposed.
	@Override
	public void handle(String counterparty, int amount) {
		
		int dailyRemaining = counterPartyDailyRemaining.get(counterparty);
		if (dailyRemaining == 0) {
//			System.out.println("Daily Limit used up on counterparty. Ignoring trade : " + createDetailsString(counterparty, amount));
//			System.out.print(".");
			// NEED TO RAISE ERROR? 
			return;
		}
		
//		String details = createDetailsString(counterparty, amount);
//		System.out.println(details);
		
		// let it blow up in null unboxing for undefined limit: fail fast.
		int tradingLimit = counterPartyTradeLimits.get(counterparty);
		

		// if trade is above limits
		if (amount > tradingLimit) {
			// Trades which are above the trading limit threshold will need 
			// to be posted against the Legacy CreditCheck system
			try {
				legacyCreditSystem.validate(counterparty, amount);
			} catch (CreditCheckException e) {
				e.printStackTrace();
				error("Trade above trading limit and couldnt inform legacy system", counterparty, amount);
				return;
			}
			
			// we're done 
			error("This trade above trading limit", counterparty, amount);
			return;
		} 
		

		// if we get here we haven't blown through the trading limit
		// check daily limit.. 
		if (amount > dailyRemaining) { 
			error("Daily limit doesnt allow this trade", counterparty, amount);
			return;
			
			// Partial trades cannot be executed however in order to satisfy the business
			// we must utilise as much of the limit as possible.
			// -- CAN WE THROW SPECIAL EXCEPTION AND TRADE SOMETHING SMALLER?
		} 
		
		
		int newDailyRemaining = dailyRemaining - amount;
		counterPartyDailyRemaining.put(counterparty, newDailyRemaining);
		
		System.out.println("Trading : "+ createDetailsString(counterparty, amount) 
				+ ": remainingDaily: " + newDailyRemaining);
		
		// update history 
		lastTrade = amount;
		
		if (amount == dailyRemaining) {
			// Once the Daily Limit is reached, fire Finished
			finish();
		} 
	}

	private void finish() {
		// fire finish event?!?
		finishedWithoutError = true;
	}
	
	private String createDetailsString( String counterparty, int amount) {
		return "CounterParty="+ counterparty+ " and amount="+ amount;
	}
	
	private void error(String message, String counterparty, int amount) {
		
		finishedWithoutError = false;
		// fire finish event?!?
//		String details = createDetailsString(counterparty, amount);
//		throw new RuntimeException(message + details);
//		System.out.println(message + ". " + details);
//		Thread.dumpStack();
	}

	

	public int getLastTrade() {
		return lastTrade;
	}


	public boolean isFinishedWithoutError() {
		return finishedWithoutError;
	}

}
