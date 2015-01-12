package com.scb.gmr.bdd;

import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@Cucumber.Options(features = {"src\\test\\java\\com\\scb\\gmr\\bdd\\features"} , glue = {"com.scb.gmr.bdd"})
public class PreDealCheckerBDDTest {
}
