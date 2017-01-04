package dg.social.ok;

import dg.social.AbstractClientConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;

/**
 * Config for OK (Odnoklassniki) client.
 * Created by gusevdm on 1/3/2017.
 */
public class OkClientConfig extends AbstractClientConfig {

    private static final Log LOG = LogFactory.getLog(OkClientConfig.class);

    @Override
    public HttpHost getProxy() {
        return null;
    }

}
