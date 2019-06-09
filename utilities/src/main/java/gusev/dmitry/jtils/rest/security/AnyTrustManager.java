package gusev.dmitry.jtils.rest.security;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/** Java trust manager. Trust any certificate. */
public class AnyTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // method is blank intentionally
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // method is blank intentionally
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
