About This Test
---------------

Everything you require to complete the test is included in this solution. In the solution you will find:

 * The instructions for the test are included in  the **PreDealChecker_Requirement** md file.
 * References to the following Test Libraries
    * jUnit
    * assertJ
    * cucumber
    * mockito
 * References to the following Libraries
    * CreditCheckAPI

 * Mocking
    * You can only use mocking on the PreDealListener Interface
      ---------------------------------------------------------

How To Set up \ Compile and Run the Project
-------------------------------------------
	* Make sure you've ANT installed
	    * ANT_HOME environment variable defined
	    * ANT command line added into your path
	* Make sure you've JDK6 or JDK7 above installed,
	    * JAVA_HOME environment variable defined,
	    * JAVA command line added into your path
	* in command line, navigate to bdd_test\Trading folder,
	* ant -p to see all the available ant targets
	* testing
        * ant alltest to run all test suites
        * ant unittest to run unit test suites
        * ant bddtest to run bdd test suites
        * simple type ant will trigger task complete to run all the tests and package the jar


