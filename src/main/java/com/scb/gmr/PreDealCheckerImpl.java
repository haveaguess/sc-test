package com.scb.gmr;

import java.util.HashMap;
import java.util.Map;

import CreditCheckAPI.CreditCheckException;
import CreditCheckAPI.CreditLimitBreach;
import CreditCheckAPI.PreDealListener;

// signal the Finished event when it has no more work (reached the daily limit)  
// signal the Error event whenever an error occurs.
public class PreDealCheckerImpl implements PreDealListener {

	// debug and error messages to stdout
	private static final boolean debug = false;

	// constructor dependancies
	private final EventBus eventBus;
	private final CreditLimitBreach legacyCreditSystem;
	private final Map<String, Integer> counterPartyTradeLimits;
	private final Map<String, Integer> counterPartyDailyLimits;
	private final Map<String, Integer> counterPartyDailyUsed = new HashMap<String, Integer>();
;
		
	// trade history
	private int lastTradeAmount = -1;


	// execution history 
	private boolean lastTradeHadError = false; 
	

	/**
	 * We can't construct without legacy system
	 * @throws CreditCheckException when legacy systems fails 
	 */
	public PreDealCheckerImpl(EventBus eventBus, CreditLimitBreach legacyCreditSystem,
			Map<String, Integer> counterPartyTradeLimits,
			Map<String, Integer> counterPartyDailyLimits) {
		this.legacyCreditSystem  = legacyCreditSystem;
		this.counterPartyTradeLimits = counterPartyTradeLimits; 	
		this.counterPartyDailyLimits = counterPartyDailyLimits; 
		this.eventBus = eventBus;
		if (debug) {
			System.out.println("Constructed a PreDealCheckerImpl");
		}
	}
	
	private boolean validateBasics(String counterparty, int amount) {
		if (checkForDailyLimitExhaustion()) {
			return false;
		}
		
		if (!counterPartyTradeLimits.containsKey(counterparty)) {
			error("The counterparty has not been setup with preauth trading limits", counterparty, amount);
			return false;
		}
		if (!counterPartyDailyLimits.containsKey(counterparty)) {
			error("The counterparty has not been setup with daily trading limits", counterparty, amount);
			return false;
		}

		return true;
	}
	
	/**
	 * 
	 * @return whether to continue trading
	 */
	private boolean handleDaily(String counterparty, int amount) {

		Integer dailyUsed = counterPartyDailyUsed.get(counterparty);
		if (dailyUsed == null) {
			dailyUsed = 0;
		}
		int dailyRemaining = counterPartyDailyLimits.get(counterparty) - dailyUsed;
		
		// "Partial trades cannot be executed however in order to satisfy the business
		// we must utilise as much of the limit as possible."
//		
		// any more trading allowed for this cparty? 
		if (dailyRemaining == 0) {
			return false;
		}
		
		// check daily limit.. 
		if (amount > dailyRemaining) { 
			error("Daily limit doesnt allow this trade", counterparty, amount);
			return false;
		} 
		
		counterPartyDailyUsed.put(counterparty, dailyUsed  + amount);
		
		if (debug) {
			System.out.println("Trading : "+ createDetailsString(counterparty, amount) 
				+ ": usedDaily: " + dailyUsed);
		}
		
		// update history 
		lastTradeAmount = amount;
		
		// Fire finished event if daily limits all used up
		if (checkForDailyLimitExhaustion()) {
			finish();
		}
		
		// regardless finish processing this trade..
		return true;
	}
	
	private boolean checkForDailyLimitExhaustion() {
		// finished if all daily limits used up
		boolean finished = true;
		for (String counterparty : counterPartyDailyUsed.keySet()) {
			int used = counterPartyDailyUsed.get(counterparty);
			int limit = counterPartyDailyLimits.get(counterparty);
			
			finished &= limit - used == 0;
		}
		 
		return finished;
	}
	
	// after firing the Finished event;  might still receive some trades
	// before being disposed.
	@Override
	public synchronized void handle(String counterparty, int amount) {
		if (!validateBasics(counterparty, amount)) {
			return;
		}
		
		/*
		 * If can't even get inside daily limits then we're done 
		 */
		if (!handleDaily(counterparty, amount)) {
			return;
		}
		
		/*
		 * Now that daily limits have been checked and are ok let's see if 
		 * we need to inform the legacy credit system...
		 */
		int preAuthorisedTradingLimit = counterPartyTradeLimits.get(counterparty);

		// if trade is above preauth limits
		if (amount > preAuthorisedTradingLimit) {
			// Trades which are above the trading limit threshold will need 
			// to be posted against the Legacy CreditCheck system for later validation
			try {
				legacyCreditSystem.validate(counterparty, amount);
			} catch (CreditCheckException e) {
				e.printStackTrace();
				error("Trade above preauth trading limit and couldnt inform legacy system", counterparty, amount);
				// we don't need to do a
				return;
			}
		} 
		
	}

	private void finish() {
		// fire finish event?!?
		eventBus.fire("FINISH", "all daily credit used up");

		lastTradeHadError = false;
	}
	
	private String createDetailsString( String counterparty, int amount) {
		return "CounterParty="+ counterparty+ " and amount="+ amount;
	}
	
	// "fire" error event for limit breach
	private void error(String message, String counterparty, int amount) {
		String details = createDetailsString(counterparty, amount);
		String errorReason = message + ". Details: " + details;

		eventBus.fire("ERROR", errorReason);
		
		if (debug) {
			Thread.dumpStack();
			System.out.println(errorReason);
//			throw new RuntimeException(message + details);
		}
		
		lastTradeHadError = true;
	}
	

	public int getLastTradeAmount() {
		return lastTradeAmount;
	}

	public boolean isLastTradeHasError() {
		return lastTradeHadError;
	}
	
	public Map<String, Integer> getCounterPartyDailyUsed() {
		return counterPartyDailyUsed;
	}
}
