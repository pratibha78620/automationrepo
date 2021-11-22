COVID19Game
This is the Automation framework setup for user authentication and leaderboard user RESTful API's

Getting Started
Prerequisites
Install the IntelliJ IDE

1. Clone the project from https://github.com/pratibha78620/automationrepo.git
2. Build the project in IntelliJ
3. Once all the required dependencies are resolved
4. run the test in automationrepo/src/test/java/herokuapp/test/HerokuappAPI.java
5. Select the test to run and right-click. choose the option select.
6. Goto Edit Configuration window
7. Make sure the expected clas and Method is selected in the Edit configuration window
8. click run/debug to run or debug the tests

NOTE: There are some limitations to the Application Authorisation API's. The Token is generated and is sent to an email
instead of showing in the response of the API. This token expires in 3days hence the fresh token has to be generated and
placed in the application.properties file.The test is not talking to the mail server hence token has to be manually pasted into the properties file.

2. Delete LeaderBoard User API requires additional header Delete-key which is not specified in the swagger document hence
the DELETE API is not fully functional

3. Test Framework has been setup using Testng and HttpClient

4. Test can be configured and run via Bamboo 

