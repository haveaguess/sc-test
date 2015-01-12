package com.scb.gmr.bdd;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class PreDealCheckerStepDefinitions {

    @Given("^a counterParty (.*)$")
    public void given_A_Counterparty(String counterParty) throws Throwable {
    }

    @And("^pre authorised trading limit of (.*)$")
    public void and_A_PreAuthorised_Trading_Limit_Of(int preAuthorised) throws Throwable {
    }

    @And("^a daily trading limit of (.*)$")
    public void and_A_Daily_Trading_Limit_Of(int dailyLimit) throws Throwable {


    }

    private void CreatePreDealChecker() {

    }

    @When("^I place the order for (.*) with a (.*)$")
    public void when_I_Place_the_Following_Order(String counterParty, int notional) throws Throwable {

    }

    @When("^I execute (.*) for (.*) with a (.*)$")
    public void when_I_Execute_the_Following_Order(int numberOfExecutions, final String counterParty, final int notional) throws Throwable {


    }


    @Then("^the trade should be successfully (.*)$")
    public void then_I_have_shared_at_hand(boolean expectedValue) throws Throwable {

    }

    @And("^the utilised daily limit should be (.*)$")
    public void and_The_Utilised_Daily_Limit_Should_Be(int utilisedDailyLimit) throws Throwable {

    }

    @Then("^I should finish within (.*)$")
    public void then_I_Should_Finish_Within(int secondsToExecute) throws Throwable {

    }


}



