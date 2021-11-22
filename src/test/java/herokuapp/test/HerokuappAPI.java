package herokuapp.test;

import com.google.gson.JsonArray;
import herokuapp.common.*;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * Created by singhp1 on 21/11/21.
 */
public class HerokuappAPI {
    private static final String USER_DATA_DIR = "./src/test/resources/user_test_data/";
    private static String auth0Token = null;
    private static String covid19TestUser = null;
    private static String covid19TestPassword = null;

    /**
     * method to login into admin portal and fetch the Auth token to verify the content index status
     */
    @BeforeClass
    public void setUserAuthToken() {
        int auth0Response = 0;

        //call the api to get the Auth0 token before running the tests
        ApiRequestFetcher fetcher = HerokuappMethods.getAuthorizationToken();

        if((fetcher.getResponsePayload().contains("error: duplicate key value violates unique constraint")) ||
                fetcher.getStatusCode() == HttpStatus.SC_OK){
            //verify the auth0 token
            auth0Response = Upload.verifyToken(PropertyLoader.AUTH0_TOKEN);
        }
        assertThat("Error - Unable to fetch valid auth0 token: ", auth0Response, is(HttpStatus.SC_OK));

        auth0Token = PropertyLoader.AUTH0_TOKEN;

        //set the username and password
        covid19TestUser = "testCovidUser" + Utils.getUniqueId();
        covid19TestPassword = "testCovidPassword" + Utils.getUniqueId();
    }


    /**
     * Test to verify user registration of covid19game
     */
    @Test(groups = {"UserRegister"})
    public void testUserRegistration() {
        //read the payload file
        String actualTestContent = FileOps.loadResource(USER_DATA_DIR + "RegisterUserPayload.json");

        //provide the username and password
        actualTestContent = actualTestContent.replace("{Covid19UserName}", covid19TestUser);
        actualTestContent = actualTestContent.replace("{COVID19PASSWORD}", covid19TestPassword);
        ApiRequestFetcher fetcher = HerokuappMethods.verifyUserRegistration(actualTestContent, auth0Token);

        //verify the response
        assertThat("Error - Unable to register the user: ", fetcher.getStatusCode(), is(HttpStatus.SC_OK));
    }

    /**
     * Test to verify user login for covid19game
     */
    @Test(groups = {"UserLogin"}, dependsOnMethods = "testUserRegistration")
    public void testUserLogin() {
        //read the payload file
        String actualTestContent = FileOps.loadResource(USER_DATA_DIR + "LoginUserPayload.json");

        //provide the username and password
        actualTestContent = actualTestContent.replace("{Covid19UserName}", covid19TestUser);
        actualTestContent = actualTestContent.replace("{COVID19Password}", covid19TestPassword);
        ApiRequestFetcher fetcher = HerokuappMethods.verifyUserLogin(actualTestContent, auth0Token);

        //verify the response
        assertThat("Error - Unable to login the app: ", fetcher.getStatusCode(), is(HttpStatus.SC_OK));
    }

    /**
     * Test to verify CRUD operations of leader board user
     */
    @Test(groups = {"UserLeaderBoard"}, dependsOnMethods = "testUserLogin")
    public void testUserLeaderBoard() {
        testUserLogin();
        //read the payload file
        String actualTestContent = FileOps.loadResource(USER_DATA_DIR + "UserCreatePayload.json");
        String playerName = "AT_Player" + Utils.getUniqueId();

        //provide the username and password
        actualTestContent = actualTestContent.replace("{PlayerName}", playerName);
        ApiRequestFetcher fetcher = HerokuappMethods.leaderBoardUserCreate(actualTestContent, auth0Token);

        //verify the response
        assertThat("Error - Unable to login the app: ", fetcher.getStatusCode(), is(HttpStatus.SC_CREATED));

        //Get the user and verify its successfully created
        JsonArray listOfLeaderBoardUsers = HerokuappMethods.getLeaderBoardUserList(auth0Token);

        boolean found = false;
        String userid = null;

        for(int i=0; i < listOfLeaderBoardUsers.size(); i++) {
            if(listOfLeaderBoardUsers.get(i).getAsJsonObject().get("username").getAsString().equalsIgnoreCase(playerName)){
                found = true;
                //get the userid for updating the player
                userid = listOfLeaderBoardUsers.get(i).getAsJsonObject().get("user_id").getAsString();
                break;
            }
        }

        assertThat("Error - unable to find user in LeaderBoard: ", found);

        //Update the user score via userId and verify the response is 204
        actualTestContent = actualTestContent.replace("0", "4000");
        fetcher = HerokuappMethods.leaderBoardUserUpdate(actualTestContent, userid, auth0Token);
        assertThat("Error - Unable to update the user: ", fetcher.getStatusCode(), is(HttpStatus.SC_NO_CONTENT));

        //delete the user
        Map<String, String> headersList = new HashMap<>();
        headersList.put("authToken", auth0Token);
        //TODO: Additional delete-key required to delete the user. Uncomment the code to verify delete request
        //headersList.put("delete-key", deleteKey);

        //fetcher = UserAuthMethods.leaderBoardUserDelete(userid, headersList);
        //assertThat("Error - Unable to update the user: ", fetcher.getStatusCode(), is(HttpStatus.SC_NO_CONTENT));
    }
}
