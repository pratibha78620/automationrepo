package herokuapp.common;

import com.google.common.base.Stopwatch;
import com.google.common.net.HttpHeaders;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static com.google.common.base.Preconditions.checkNotNull;
import static herokuapp.common.Utils.timeDelay;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;


/**
 * Takes in an HttpUriRequest and executes it. Parses the response into the appropriate type
 * (for now, either an ImageDTO or a String.)
 *
 * Commonly used with ApiRequestBuilder and MasheryRequestBuilder.
 */
public class ApiRequestFetcher {
	private static final int MAXIMUM_RETRY_ATTEMPTS = 3;
	private static int requestCounter;
    private static int requestRetryCounter;
	private static Logger Log = Logger.getLogger(ApiRequestFetcher.class);
	private CloseableHttpResponse response;
	private HttpUriRequest request;
	CloseableHttpClient client;

	public byte[] getPayload() {
		return payload;
	}

	/** Where the response is stored in raw form. */
	private byte[] payload;

	/** These are initialised depending on the response content type. */
	private String responsePayLoad;
	private Image imagePayload;

	/**
	 * Executes the required request.
	 *
	 * @param request The request to execute. Must not be null.
	 */
	public ApiRequestFetcher(HttpUriRequest request) {
		checkNotNull(request);

		this.request = request;
		request.addHeader("accept", "application/json");
		executeRequest();
	}

	/**
	 * Executes the request this object was initialised with.
	 */
	public void executeRequest() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			client = HttpClientPool.getClient();
			/* Time the request */
			Reporter.log("Request to: " + request.toString(), true);
			response = client.execute(request);
			Reporter.log("**response from executeRequest : " + response.toString(), true);
			Reporter.log("Execution completed at: " + Utils.getIsoTimestamp(0), true);
            requestCounter++;
			stopwatch.stop();
			Reporter.log("Time taken: " + stopwatch + " status code: " + getStatusCode(), true);

			if (getStatusCode() == HttpStatus.SC_GATEWAY_TIMEOUT || getStatusCode() == HttpStatus.SC_BAD_GATEWAY) {
				Reporter.log("Retrying due to gateway timeout or bad gateway......", true);
				retryAttempts();
			}

			if(getStatusCode() == 202){
				Reporter.log("Response received at: " + Utils.getIsoTimestamp(0), true);
				return;
			}

			readDataIntoPayloadArray();
			setResponsePayload();
		} catch(NoHttpResponseException noRespExcep) {
				Reporter.log("NoHttpResponseException found....", true);
				noRespExcep.printStackTrace();
		}
		catch (Exception e) {
			Reporter.log("Exception from request fetcher - Time taken: " + stopwatch, true);
			e.printStackTrace();
			System.out.println("error - e.getMessage(): " + e.getMessage());
			if(e.getMessage().contains("Connection reset")){
				for(int retry = 0; retry < MAXIMUM_RETRY_ATTEMPTS; retry++) {
					System.out.println("Retry again as the connection reset error thrown...");
					try {
						executeRequest();
						break;
					} catch (RuntimeException runtimeexp) {
						System.out.println("eruntimeexp: " + runtimeexp);
					}
				}
			}
		}

	}

	/**
	 * Executes the request with proxy settings this object was initialised with.
	 */
	public void executeProxyRequest() {

		Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			client = HttpClientPool.getClient();
			/* Time the request */
		Reporter.log("Request to: " + request.toString(), true);
		response = client.execute(request);
		Reporter.log("**response from executeRequest : " + response.toString(), true);
		Reporter.log("Execution completed at: " + Utils.getIsoTimestamp(0), true);
		requestCounter++;
		stopwatch.stop();
		Reporter.log("Time taken: " + stopwatch + " status code: " + getStatusCode(), true);

		if (getStatusCode() == HttpStatus.SC_GATEWAY_TIMEOUT || getStatusCode() == HttpStatus.SC_BAD_GATEWAY) {
			Reporter.log("Retrying due to gateway timeout or bad gateway......", true);
			retryAttempts();
		}

		if(getStatusCode() == 202){
			Reporter.log("Response received at: " + Utils.getIsoTimestamp(0), true);
			return;
		}

		readDataIntoPayloadArray();
		if (getContentType() != null && getContentType().toLowerCase().contains("image")) {
			setImagePayload();
		} else {
			setResponsePayload();
		}
	} catch(NoHttpResponseException noRespExcep) {
		Reporter.log("NoHttpResponseException found....", true);
		noRespExcep.printStackTrace();
	}
	catch (Exception e) {
		Reporter.log("Exception from request fetcher - Time taken: " + stopwatch, true);
		e.printStackTrace();
		System.out.println("error - e.getMessage(): " + e.getMessage());
		if(e.getMessage().contains("Connection reset")){
			for(int retry = 0; retry < MAXIMUM_RETRY_ATTEMPTS; retry++) {
				System.out.println("Retry again as the connection reset error thrown...");
				try {
					executeRequest();
					break;
				} catch (RuntimeException runtimeexp) {
					System.out.println("eruntimeexp: " + runtimeexp);
				}
			}
		}
	}

}

	/**
	 * Read the response into a byte array.
	 */
	private void readDataIntoPayloadArray() {
		try{
			if(response.getEntity() != null) {
				InputStream in = response.getEntity().getContent();
				assertThat("Error inputStream is Null: ", in, is(notNullValue()));
				payload = IOUtils.toByteArray(in);
				EntityUtils.consumeQuietly(response.getEntity());
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
	}

	/**

	/**
	 * @return The HTTP response status code.
	 */
	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}

	/**
	 * @return Whether the request was successful.
	 */
	public boolean isRequestSuccessful() {
		return (getStatusCode() == HttpStatus.SC_OK);
	}

	/**
	 * @return The content type of the response.
	 */
	String getContentType() {
		if (response.getFirstHeader(HttpHeaders.CONTENT_TYPE) != null) {
			return response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getResponsePayload() {
		if (!(isRequestSuccessful() || getStatusCode() == HttpStatus.SC_CREATED)) {
			Reporter.log("request headers: " + response.getAllHeaders(), true);
			Reporter.log("Request response: " + response.getStatusLine(), true);
		}
		return this.responsePayLoad;
	}

	private void setResponsePayload() {
		if (payload == null) {
			this.responsePayLoad = null;
			return;
		}

		try {
			this.responsePayLoad = new String(payload, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee.getMessage());
		}
	}

	private void setImagePayload() {
		try (InputStream stream = new ByteArrayInputStream(payload)) {
			this.imagePayload = ImageIO.read(stream);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
	}

	/**
	 * Retries a request if a 504/503/443 response is returned
	 */
	private void retryAttempts() throws IOException {
		int counter = 0;
        requestRetryCounter++;
		while (counter < MAXIMUM_RETRY_ATTEMPTS) {
			if (getStatusCode() == HttpStatus.SC_GATEWAY_TIMEOUT || getStatusCode() == HttpStatus.SC_BAD_GATEWAY) {
				timeDelay(600);
				Reporter.log("Network issues (status code " + getStatusCode() + "), trying again... ("+request.getURI()+")", true);
				response = HttpClientPool.getClient().execute(request);
				counter++;
			} else {
				return;
			}
		}
		while (counter < MAXIMUM_RETRY_ATTEMPTS) {
			timeDelay(20000);
			Reporter.log("Network issues" + " trying again... ("+request.getURI()+")", true);
			HttpClientPool.getClient().execute(request);
			counter++;
		}
		throw new RuntimeException("URL " + request.getURI() + " could not be successfully requested.");
	}
}