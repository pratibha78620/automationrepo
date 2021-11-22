package herokuapp.common;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Class to store a multithreaded http client, shareable across threads.
 * Get the HTTP client by calling getClient.
 */
public class HttpClientPool {
	private static final int MAX_TOTAL_CONNECTIONS = 5;
	private static final int MAX_CONNECTIONS_PER_ROUTE = 5;

	private static volatile HttpClientPool instance = new HttpClientPool();

	private CloseableHttpClient httpClient;

	private HttpClientPool() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000)
				.setSocketTimeout(5000)
				.build();
		HttpClientBuilder clientBuilder = HttpClientBuilder.create()
				.setConnectionManager(getMultithreadedConnectionManager())
				.setRedirectStrategy(new DefaultRedirectStrategy())
				.setDefaultRequestConfig(requestConfig)
				.disableCookieManagement()
				.disableAutomaticRetries()
				.disableRedirectHandling()
				.setSSLHostnameVerifier(new NoopHostnameVerifier());

		httpClient = clientBuilder.build();
	}

	/**
	 * Get a connection manager.
	 * @return the connection manager created.
	 */
	private static PoolingHttpClientConnectionManager getMultithreadedConnectionManager() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// Increase max total connection
		cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		// Increase default max connection per route to 5
		cm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
		cm.setValidateAfterInactivity(500);
		return cm;
	}

	/**
	 * Returns a multithreaded HTTP client.
	 * @return An HTTP client
	 */
	public static CloseableHttpClient getClient() {
		return instance.httpClient;
	}
}
