package dg.social;

import org.apache.http.HttpHost;

/**
 * Base config class for social networks client's configs.
 * Created by gusevdm on 1/3/2017.
 */

public abstract class AbstractClientConfig {

    /** Returns proxy host for social network client http requests. */
    public abstract HttpHost getProxy();

}
