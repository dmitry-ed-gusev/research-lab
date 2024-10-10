package dmitry.gusev.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * SSLClientCreator illustrates the basic approach for creating a client using SSL for database access.
 * <p>
 * Note:  to run this example, you must modify the REST server by specifying a SSL certificate template.
 */
public class SSLClientCreator {
    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        run(Util.loadProperties());
    }

    public static void run(Util.ExampleProperties props) throws NoSuchAlgorithmException, KeyManagementException {
        System.out.println("example: " + SSLClientCreator.class.getName());

        // create a trust manager
        // (note: a real application should verify certificates)
        TrustManager naiveTrustMgr = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        // create an SSL context
        SSLContext sslContext = SSLContext.getInstance("SSLv3");
        sslContext.init(null, new TrustManager[]{naiveTrustMgr}, null);

        // create the client
        // (note: a real application should use a COMMON, STRICT, or implemented hostname verifier)
        DatabaseClient client = DatabaseClientFactory.newClient(
                props.host, props.port, props.writerUser, props.writerPassword,
                Authentication.DIGEST, sslContext, SSLHostnameVerifier.ANY);

        // make use of the client connection
        TextDocumentManager docMgr = client.newTextDocumentManager();
        String docId = "/example/text.txt";
        StringHandle handle = new StringHandle();
        handle.set("A simple text document");
        docMgr.write(docId, handle);

        System.out.println(
                "Connected by SSL to " + props.host + ":" + props.port + " as " + props.writerUser);

        // clean up the written document
        docMgr.delete(docId);

        // release the client
        client.release();
    }
}
