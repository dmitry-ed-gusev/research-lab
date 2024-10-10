package dg.social.crawler.networks.telescope;

import dg.social.crawler.networks.AbstractClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

import static dg.social.crawler.SCrawlerDefaults.CRAWLER_CONFIG_BEAN;

/**
 * Config for Telescope client.
 * Created by gusevdm on 3/17/2017.
 */

@Service
public class TelescopeClientConfig extends AbstractClientConfig {

    // config prefix for Telescope parameters (for Properties)
    private static final String DEFAULT_CONFIG_PREFIX   = "ts";

    /***/
    @Autowired
    public TelescopeClientConfig(@Qualifier(CRAWLER_CONFIG_BEAN) Properties properties) {
        super(DEFAULT_CONFIG_PREFIX, properties);
    }

    @Override
    public String getAccessTokenRequest() {
        // todo: method implementation!
        throw new IllegalStateException("Method is unsupported yet!");
    }

    @Override
    public String getBaseApiRequest() {
        // todo: method implementation!
        throw new IllegalStateException("Method is unsupported yet!");
    }

    @Override
    public String getApiVersion() {
        // todo: method implementation!
        throw new IllegalStateException("Method is unsupported yet!");
    }

}
