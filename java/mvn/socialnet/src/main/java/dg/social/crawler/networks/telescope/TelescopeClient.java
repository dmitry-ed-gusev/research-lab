package dg.social.crawler.networks.telescope;

import dg.social.crawler.networks.AbstractClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Telescope social network/system client.
 * Created by gusevdm on 3/17/2017.
 */

@Service
public class TelescopeClient extends AbstractClient {

    private static final Log LOG = LogFactory.getLog(TelescopeClient.class);

    /***/
    @Autowired
    public TelescopeClient(TelescopeClientConfig config, TelescopeFormsRecognizer formRecognizer) {
        super(config, formRecognizer);
    }

}
