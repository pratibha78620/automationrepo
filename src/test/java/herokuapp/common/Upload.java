package herokuapp.common;

import com.google.common.net.HttpHeaders;
import jodd.util.MimeTypes;
import org.testng.Reporter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class Upload {

	/**
	 * method to build the ingestion call builder for ingesting content
	 * @param requestAction
     * @return
     */
	public static ApiRequestBuilder contentIngestionCallBuilder(HttpMethods requestAction,
																  String contentType) {
		URI docURI = null;
		switch(contentType)
		{
			case "userLogin":
				docURI = Utils.convertStringToUri(PropertyLoader.HEROKUAPP_AUTH_USER_LOGIN);
				break;
			case "userRegister":
				docURI = Utils.convertStringToUri(PropertyLoader.HEROKUAPP_AUTH_USER_REGISTER);
				break;
			case "leaderBoard":
				docURI = Utils.convertStringToUri(PropertyLoader.HEROKUAPP_LEADERBOARD_V1_USER);
				break;
		}
		return new AkamaiRequestBuilder(requestAction, docURI);
	}


	/**
	 * method to post User Authentication
	 * @param content
	 * @param headersList
     * @return
     */
	public static ApiRequestFetcher createOrUpdateUser(String content, String userId, String contentType,
															Map<String, String> headersList){
		ApiRequestBuilder requestBuilder;

		if(userId == null) {
			requestBuilder = contentIngestionCallBuilder(HttpMethods.POST, contentType);
		} else {
			requestBuilder = contentIngestionCallBuilder(HttpMethods.PUT, contentType);
		}

		//set the content
		requestBuilder.setContent(content);

		//Setup the headers
		requestBuilder.addHeader(HttpHeaders.CONTENT_TYPE, MimeTypes.MIME_APPLICATION_JSON);
		requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, headersList.get("authToken"));

		ApiRequestFetcher fetcher = requestBuilder.execute();

		return fetcher;
	}

	/**
	 * method to delete user in leaderboard
	 * @param userId
	 * @param contentType
	 * @param headersList
     * @return
     */
	public static ApiRequestFetcher deleteUser(String userId, String contentType,
											   Map<String, String> headersList){
		ApiRequestBuilder requestBuilder;
		requestBuilder = contentIngestionCallBuilder(HttpMethods.DELETE, contentType);

		if(userId != null)
		{
			requestBuilder.appendPath(userId);
		}
		//Setup the headers
		requestBuilder.addHeader(HttpHeaders.CONTENT_TYPE, MimeTypes.MIME_APPLICATION_JSON);
		requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, headersList.get("authToken"));

		Reporter.log("Executing DELETE request for" + userId, true);
		return requestBuilder.execute();
	}

	/**
	 * method to verify if auth0 token is valid
	 * @return integer
	 */
	public static int verifyToken(String auth0Token){
		ApiRequestFetcher fetcher = null;
		try {
			fetcher = new AkamaiRequestBuilder(HttpMethods.GET,
					new URI(PropertyLoader.AUTH0_VERIFY_TOKEN))
					.addHeader(org.apache.http.HttpHeaders.AUTHORIZATION, auth0Token)
					.execute();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return fetcher.getStatusCode();
	}
}
