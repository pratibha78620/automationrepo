package herokuapp.common;

import com.sun.net.ssl.HostnameVerifier;

import javax.net.ssl.SSLSession;

/**
 * Created by singhp1 on 22/11/21.
 */
public class NullHostNameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }

    @Override
    public boolean verify(String s, String s1) {
        return false;
    }
}
