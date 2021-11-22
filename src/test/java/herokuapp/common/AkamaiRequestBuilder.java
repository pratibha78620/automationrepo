package herokuapp.common;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;

/**
 * Builder class used for making API requests. Calling build will return an HttpUriRequest,
 * which can be used with the ApiRequestFetcher class to retrieve the results of the request.
 */
public class AkamaiRequestBuilder extends ApiRequestBuilder {
    protected static Logger log = Logger.getLogger(AkamaiRequestBuilder.class.getName());


    /**
     * @param method
     * @param endpoint
     */
    public AkamaiRequestBuilder(HttpMethods method, URI endpoint) {
        super(method, endpoint);
    }

    /**
     * @param value
     */
    @Override
    public AkamaiRequestBuilder appendPath(String value) {
        super.appendPath(value);
        return this;
    }

    /**
     * @param value
     */
    @Override
    public AkamaiRequestBuilder setPath(String value) {
        super.setPath(value);
        return this;
    }

    /**
     * @param value
     */
    @Override
    public AkamaiRequestBuilder appendURI(String value) {
        super.appendURI(value);
        return this;
    }

    /**
     * @param parameter
     * @param value
     * @return
     */
    @Override
    public AkamaiRequestBuilder addParam(String parameter, String value) {
        super.addParam(parameter, value);
        return this;
    }

    /**
     * Adds the specified URL parameters from the string.
     *
     * @param params The string containing the URL parameters to add. Must be in the
     *               format "name1=value1,name2=value2,name3=value3,..."
     * @return
     */
    @Override
    public AkamaiRequestBuilder addParams(String params) {
        super.addParams(params);
        return this;
    }

    /**
     * Sets the bustTime parameter with the current time.
     *
     * @param cacheBust
     */
    @Override
    public AkamaiRequestBuilder setCacheBuster(boolean cacheBust) {
        super.setCacheBuster(cacheBust);
        return this;
    }

    /**
     * @param header
     * @param value
     */
    @Override
    public AkamaiRequestBuilder addHeader(String header, String value) {
        super.addHeader(header, value);
        return this;
    }

    /**
     * @param parameter
     * @param value
     */
    @Override
    public AkamaiRequestBuilder setParam(String parameter, String value) {
        super.setParam(parameter, value);
        return this;
    }

    @Override
    public AkamaiRequestBuilder removeParam(String parameter) {
        super.removeParam(parameter);
        return this;
    }

    /**
     * @param endpoint
     */
    @Override
    public AkamaiRequestBuilder setUri(String endpoint) {
        super.setUri(endpoint);
        return this;
    }

    /**
     * @param endpoint
     */
    @Override
    public AkamaiRequestBuilder setUri(URI endpoint) {
        super.setUri(endpoint);
        return this;
    }

    /**
     * @param username
     * @param password
     */
    @Override
    public AkamaiRequestBuilder setCredentials(String username, String password) {
        super.setCredentials(username, password);
        return this;
    }

    /*
     * @param value
     */
    @Override
    public AkamaiRequestBuilder setLoginRequired(boolean value) {
        super.setLoginRequired(value);
        return this;
    }

    /**
     * @param contentFile
     */
    @Override
    public AkamaiRequestBuilder setContentFile(File contentFile) {
        super.setContentFile(contentFile);
        return this;
    }

    /**
     * @param content
     */
    @Override
    public AkamaiRequestBuilder setContent(String content) {
        super.setContent(content);
        return this;
    }

    /**
     * @return An HttpUriRequest representing this object.
     */
    public HttpUriRequest build() {
        return super.build();
    }
}
