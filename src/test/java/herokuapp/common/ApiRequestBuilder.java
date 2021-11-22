package herokuapp.common;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static herokuapp.common.StringUtil.split;

/**
 * Builder class for constructing API requests. Commonly used with the ApiRequestFetcher for executing requests.
 * For an API request that uses Akamai, use the AkamaiRequestBuilder.
 */
public class ApiRequestBuilder {
	protected static Logger log = Logger.getLogger(ApiRequestBuilder.class.getName());

	/* When adding a field to this list, make sure to add it to the cloning method! (The constructor which
	 * takes in another ApiRequestBuilder  */
	protected List<NameValuePair> headers;
	protected List<NameValuePair> urlParameters;
	protected String path = "";

	protected HttpMethods action;
	protected String content = "";
	protected File contentFile;
	protected URI uri;
	protected HttpEntity entity;
	protected boolean loginRequired;
	protected UsernamePasswordCredentials credentials;

	protected boolean cacheBust = false;

	/**
	 * @param method   The method to use for the request. Must not be null.
	 * @param endpoint The endpoint to use for the request. Must not be null.
	 */
	public ApiRequestBuilder(HttpMethods method, URI endpoint) {
		checkNotNull(method);
		checkNotNull(endpoint);

		headers = new ArrayList<>();
		urlParameters = new ArrayList<>();

		this.action = method;
		this.uri = endpoint;
	}

	/**
	 * @param value The string to append to the path. Must not be null.
	 */
	public ApiRequestBuilder appendPath(String value) {
		checkNotNull(value);

		if (path.isEmpty() || path.substring(path.length() - 1).equals("/")) {
			path += value;
		} else {
			path += '/' + value;
		}
		return this;
	}

	/**
	 * @param value The string to set the path to. Must not be null.
	 */
	public ApiRequestBuilder setPath(String value) {
		checkNotNull(value);

		path = value;
		return this;
	}

	/**
	 * @param value The string to append to the URI. Must not be null.
	 */
	public ApiRequestBuilder appendURI(String value) {
		checkNotNull(value);

		uri = Utils.convertStringToUri(uri.toString() + value);
		return this;
	}

	/**
	 * Adds a parameter to the request. Note that it will pass it as a GET parameter - this class
	 * does not yet support POST parameters.
	 * <p/>
	 * If the specified parameter already exists, then the parameter-value pair will be added anyway.
	 * This doesn't overwrite the previous value - just adds an additional value to the parameter.
	 * This can result in errors if the endpoint is not able to take more than one value per parameter.
	 *
	 * @param parameter The name of the parameter to add. Must not be null.
	 * @param value     The value of the parameter to add. Can be null.
	 * @return An ApiRequestBuilder as per the builder pattern.
	 */
	public ApiRequestBuilder addParam(String parameter, String value) {
		checkNotNull(parameter);

		urlParameters.add(new BasicNameValuePair(parameter, value));
		return this;
	}

	/**
	 * Removes a parameter (and all of its values).
	 *
	 * @param parameter The parameter to delete.
	 * @return An ApiRequestBuilder as per the builder pattern.
	 */
	public ApiRequestBuilder removeParam(String parameter) {
		checkNotNull(parameter);

		Iterator<NameValuePair> it = urlParameters.iterator();

		NameValuePair pair;
		while (it.hasNext()) {
			pair = it.next();
			if (pair.getName().equals(parameter)) {
				it.remove();
			}
		}
		return this;
	}


	/**
	 * Adds the specified URL parameters from the string.
	 *
	 * @param params The string containing the URL parameters to add. Must be in the
	 *               format "name1=value1,name2=value2,name3=value3,..."
	 * @return An ApiRequestBuilder as per the builder pattern.
	 */
	public ApiRequestBuilder addParams(String params) {
		checkNotNull(params);

		this.setCacheBuster(true);
		for (String pair : split(",", params)) {
			String keyValue[] = pair.split("=");
			this.addParam(keyValue[0], keyValue[1]);
		}
		return this;

	}

	/**
	 * Sets the bustTime parameter with the current time.
	 */
	public ApiRequestBuilder setCacheBuster(boolean cacheBust) {
		this.cacheBust = cacheBust;
		return this;
	}

	/**
	 * Overrides a parameter's value if it already exists. If the parameter does not already exist,
	 * then this function will not add the parameter-value pair. This must be done outside of the function
	 * (i.e. if this function returns false).
	 *
	 * @param parameter The parameter to check.
	 * @param value     The value to set the parameter to.
	 * @return Returns true if the parameter specified already exists; false if not.
	 */
	protected boolean overrideParameterValueIfAlreadyExists(String parameter, String value) {
		for (int i = 0; i < urlParameters.size(); i++) {
			if (urlParameters.get(i).getName().equals(parameter)) {
				urlParameters.remove(i);
				addParam(parameter, value);
				return true;
			}
		}
		return false;
	}

	/**
	 * @param header The name of the header to add. Must not be null.
	 * @param value  The value of the header to add. Must not be null.
	 */
	public ApiRequestBuilder addHeader(String header, String value) {
		checkNotNull(header);
		checkNotNull(value);

		headers.add(new BasicNameValuePair(header, value));
		return this;
	}

	/**
	 * Sets a parameter value. Unlike addParam, setParam will delete the previous value
	 * of the parameter if it already exists.
	 *
	 * @param parameter The name of the parameter to set. Must not be null.
	 * @param value     The value of the parameter to set. Can be null.
	 */
	public ApiRequestBuilder setParam(String parameter, String value) {
		checkNotNull(parameter);

		if (!overrideParameterValueIfAlreadyExists(parameter, value)) {
			addParam(parameter, value);
		}

		return this;
	}

	/**
	 * @param endpoint What to change the URI to. Must not be null.
	 */
	public ApiRequestBuilder setUri(String endpoint) {
		checkNotNull(endpoint);

		this.uri = Utils.convertStringToUri(endpoint);
		return this;
	}

	/**
	 * @param endpoint What to set the URI to. Must not be null.
	 */
	public ApiRequestBuilder setUri(URI endpoint) {
		checkNotNull(endpoint);

		this.uri = endpoint;
		return this;
	}

	/**
	 * @param username Must not be null.
	 * @param password Must not be null.
	 */
	public ApiRequestBuilder setCredentials(String username, String password) {
		checkNotNull(username);
		checkNotNull(password);

		this.loginRequired = true;
		this.credentials = new UsernamePasswordCredentials(username, password);
		return this;
	}

	/**
	 * @param value
	 */
	public ApiRequestBuilder setLoginRequired(boolean value) {
		this.loginRequired = value;
		return this;
	}

	/**
	 * @return
	 */
	public File getContentFile() {
		return contentFile;
	}

	/**
	 * @param contentFile Must not be null.
	 */
	public ApiRequestBuilder setContentFile(File contentFile) {
		checkNotNull(contentFile);

		this.contentFile = contentFile;
		return this;
	}

	/**
	 * @return
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content Must not be null.
	 */
	public ApiRequestBuilder setContent(String content) {
		checkNotNull(content);

		this.content = content;
		return this;
	}

	/**
	 * Executes the current request.
	 */
	public ApiRequestFetcher execute() {
		return new ApiRequestFetcher(this.build());
	}

	/**
	 * @return An HttpUriRequest based on this API request.
	 */
	public HttpUriRequest build() {
		URI requestUri = getUriWithParameters();
		RequestBuilder reqBuilder = RequestBuilder
				.create(String.valueOf(action))
				.setUri(requestUri);

		addHeadersToRequestBuilder(reqBuilder, headers);

		addActionSpecificData(reqBuilder);

		return reqBuilder.build();
	}

	/**
	 * Adds action-appropriate entities to the given RequestBuilder.
	 * e.g., if the action "POST_FILE" is specified, then the relevant file
	 * is added to the request builder.
	 *
	 * @param reqBuilder The RequestBuilder to add entities to. Must not be null.
	 */
	private void addActionSpecificData(RequestBuilder reqBuilder) {
		checkNotNull(reqBuilder);

		if (entity != null) {
			reqBuilder.setEntity(entity);
			return;
		}

		switch (action) {
			case DELETE:
			case PUT:
				reqBuilder.setEntity(new StringEntity(content, Charset.forName("UTF-8")));
				break;
			case POST:
				if (contentFile == null)
					reqBuilder.setEntity(new StringEntity(content, Charset.forName("UTF-8")));
				else {
					MultipartEntityBuilder entity = MultipartEntityBuilder.create();
					entity.addPart("file", new FileBody(contentFile));
					reqBuilder.setEntity(entity.build());
				}
				break;
		}
	}

	/**
	 * Adds the headers to the request builder.
	 *
	 * @param builder The RequestBuilder to add the headers to. Must not be null.
	 * @param headers The headers to add. Must not be null.
	 */
	private void addHeadersToRequestBuilder(RequestBuilder builder, List<NameValuePair> headers) {
		checkNotNull(builder);
		checkNotNull(headers);

		for (NameValuePair header : headers) {
			builder.addHeader(header.getName(), header.getValue());
		}
	}

	/**
	 * Append the parameters to the URI.
	 *
	 * @return A URI with the parameters appended to the original URI.
	 */
	private URI getUriWithParameters() {
		URIBuilder uriBuilder = new URIBuilder(uri)
				.setPath(uri.getPath() + path)
				.addParameters(urlParameters);

		try {
			return uriBuilder.build();
		} catch (URISyntaxException use) {
			throw new RuntimeException("URI syntax exception: " + uriBuilder.toString());
		}
	}
}