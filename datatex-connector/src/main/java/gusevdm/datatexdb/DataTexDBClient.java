package gusevdm.datatexdb;

import gusevdm.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** DataTex DB client. */
public class DataTexDBClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTexDBClient.class);

    // internal state
    //private final String dbHost;
    //private final String dbPort;
    //private final String dbUser;
    //private final String dbPass;
    //private final String dbSchema;

    public DataTexDBClient() {
        LOGGER.debug("DataTexDBClient constructor() is working.");
        Environment env = Environment.getInstance();
        //this.path = env.getLuxMSURL();
        //this.user = env.getLuxMSUser();
        //this.password = env.getLuxMSPassword();
    }

    /***/
    public String getTablesList() {
        LOGGER.debug("DataTexClient.getTablesList() is working.");

        return "[not implemented yet!]";
    }

}
