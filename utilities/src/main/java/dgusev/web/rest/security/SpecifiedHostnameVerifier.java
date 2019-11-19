package dgusev.web.rest.security;

import lombok.NonNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**  Hostname verifier - successfully verify specified host (any).*/
public final class SpecifiedHostnameVerifier implements HostnameVerifier {

    private static final Log LOGGER = LogFactory.getLog(SpecifiedHostnameVerifier.class);

    private final String host;

    public SpecifiedHostnameVerifier(@NonNull String host) {
        LOGGER.debug(String.format("SpecifiedHostnameVerifier constructor() is working. Host [%s].", host));
        this.host = host;
    }

    @Override
    public boolean verify(String hostname, SSLSession sslSession) {

        if (hostname.equals(this.host)) {
            LOGGER.debug(String.format("Verifying host [%s]. Result: TRUE.", hostname));
            return true;
        }

        LOGGER.debug(String.format("Verifying host [%s]. Result: FALSE.", hostname));
        return false;
    }

}
