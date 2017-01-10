package dg.social;

/**
 * Base abstract class for social networks clients.
 * Created by gusevdm on 1/10/2017.
 */

public class AbstractClient {

    private AbstractClientConfig config;

    /***/
    public AbstractClient(AbstractClientConfig config) {

        if (config == null) { // fail-fast
            throw new IllegalArgumentException("Config cannot be NULL!");
        }

        this.config = config;
    }

    public AbstractClientConfig getConfig() {
        return config;
    }

}
