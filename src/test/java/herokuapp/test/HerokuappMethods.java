package herokuapp.test;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import herokuapp.common.*;
import jodd.util.MimeTypes;
import org.apache.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by singhp1 on 21/11/21.
 */
public class HerokuappMethods {

    /**
     * method to generate auth token
     */
    public static ApiRequestFetcher getAuthorizationToken(){
        String testContent = FileOps.loadResource("./src/test/resources/user_test_data/auth0TestPayload.json");
        testContent = testContent.replace("{AUTH0_KEY}", PropertyLoader.AUTH0_KEY);
        testContent = testContent.replace("{AUTH0_EMAILADDRESS}", PropertyLoader.AUTH0_EMAILADDRESS);

        ApiRequestBuilder requestBuilder = HerokuappMethods.auth0TokenCallBuilder();

        //set the headers
        /*requestBuilder.addHeader("accept", "application/json");*/
        requestBuilder.addHeader(HttpHeaders.CONTENT_TYPE, MimeTypes.MIME_APPLICATION_JSON);

        //set the content of API
        requestBuilder.setContent(testContent);

        //execute the API request
        ApiRequestFetcher fetcher = requestBuilder.execute();
        return fetcher;
    }

    /**
     * Method to build the auth0 token request
     * @return
     */
    private static ApiRequestBuilder auth0TokenCallBuilder() {
        URI docURI = Utils.convertStringToUri(PropertyLoader.AUTH0_GENERATE_TOKEN);
        return new AkamaiRequestBuilder(HttpMethods.POST, docURI);
    }

    /**
     * Method to verify covid19 User registration
     * @param userPayload
     */
    public static ApiRequestFetcher verifyUserRegistration(String userPayload, String auth0Token) {
        //Call the User Register API via POST
        Map<String, String> headersList = new HashMap<>();
        headersList.put("authToken", auth0Token);
        ApiRequestFetcher fetcher = Upload.createOrUpdateUser(userPayload, null, "userRegister", headersList);

        return fetcher;
    }

    /**
     * Method to login into covid19game app
     * @param userPayload
     * @param auth0Token
     * @return
     */
    public static ApiRequestFetcher verifyUserLogin(String userPayload, String auth0Token) {
        //Call the User Login API via POST
        Map<String, String> headersList = new HashMap<>();
        headersList.put("authToken", auth0Token);
        ApiRequestFetcher fetcher = Upload.createOrUpdateUser(userPayload, null, "userLogin", headersList);

        return fetcher;
    }

    /**
     * Method to create player for the game
     * @param userPayload
     * @param auth0Token
     * @return
     */
    public static ApiRequestFetcher leaderBoardUserCreate(String userPayload, String auth0Token) {
        //Call the User Create API via POST
        Map<String, String> headersList = new HashMap<>();
        headersList.put("authToken", auth0Token);
        ApiRequestFetcher fetcher = Upload.createOrUpdateUser(userPayload, null, "leaderBoard", headersList);

        return fetcher;
    }

    /**
     * Method to retrieve list of leaderboard users
     * @param auth0Token
     * @return
     */
    public static JsonArray getLeaderBoardUserList(String auth0Token) {
        //Call the User Create API via POST
        ApiRequestFetcher fetcher;
        AkamaiRequestBuilder requestBuilder = null;
        try{
            requestBuilder = new AkamaiRequestBuilder(HttpMethods.GET,
                    new URI(PropertyLoader.HEROKUAPP_LEADERBOARD_V1_USER))
                    .addHeader(org.apache.http.HttpHeaders.AUTHORIZATION, auth0Token);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        fetcher = requestBuilder.execute();

        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(fetcher.getResponsePayload());

        assertThat("Error - Unable to get leader board Users List: ", fetcher.getStatusCode(), is(HttpStatus.SC_OK));

        return jsonArray;
    }

    /**
     * Method to update the leaderBoard user
     * @param userPayload
     * @param auth0Token
     * @return
     */
    public static ApiRequestFetcher leaderBoardUserUpdate(String userPayload, String userId, String auth0Token) {
        //Call the User Create API via POST
        Map<String, String> headersList = new HashMap<>();
        headersList.put("authToken", auth0Token);
        ApiRequestFetcher fetcher = Upload.createOrUpdateUser(userPayload, userId, "leaderBoard", headersList);

        return fetcher;
    }

    public static ApiRequestFetcher leaderBoardUserDelete(String userId, Map<String, String> headersList) {
        //Call the User Delete API via DELETE
        ApiRequestFetcher fetcher = Upload.deleteUser(userId, "leaderBoard", headersList);

        return fetcher;
    }
}
