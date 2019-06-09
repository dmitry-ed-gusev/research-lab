package gusev.dmitry.jtils.rest.security;

import lombok.NonNull;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**  Hostname verifier - successfully verify specified host (any).*/
public final class SpecifiedHostnameVerifier implements HostnameVerifier {

    private final String host;

    public SpecifiedHostnameVerifier(@NonNull String host) {
        this.host = host;
    }

    @Override
    public boolean verify(String hostname, SSLSession sslSession) {
        if (hostname.equals(this.host)) {
            return true;
        }
        return false;
    }

}
