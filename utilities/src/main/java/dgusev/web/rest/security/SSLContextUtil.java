package dgusev.web.rest.security;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/***/
public final class SSLContextUtil {

    private SSLContextUtil() {} // non-instanceability

    /***/
    public static SSLContext getInsecureSSLContext() throws KeyManagementException, NoSuchAlgorithmException {

        //
        final TrustManager[] trustAllCerts = new TrustManager[] {

                //
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(final java.security.cert.X509Certificate[] arg0, final String arg1)
                            throws CertificateException {
                        // do nothing and blindly accept the certificate
                    }

                    public void checkServerTrusted(final java.security.cert.X509Certificate[] arg0, final String arg1)
                            throws CertificateException {
                        // do nothing and blindly accept the server
                    }

                } // end of X509TrustManager

        }; // end of "trust all certs" trust manager

        // init SSL context
        final SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());

        return sslcontext;
    }

}
