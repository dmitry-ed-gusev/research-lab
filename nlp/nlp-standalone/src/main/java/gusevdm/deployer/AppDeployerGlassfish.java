package gusevdm.deployer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

import java.io.File;

/**
 * Class deploys WAR archive (application) with GlassFIsh JEE container in embedded mode.
 */
public class AppDeployerGlassfish {

    private static final Log LOG = LogFactory.getLog(AppDeployerGlassfish.class);

    /***/
    public static void deployWebService(String warFile, String context, int port) throws GlassFishException {
        LOG.debug("AppDeployerGlassfish.deployWebService() is working.");

        if (StringUtils.isBlank(warFile) || !new File(warFile).exists() || StringUtils.isBlank(context)) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("Empty war name [%s], war not exists or empty context [%s]!", warFile, context));
        }

        LOG.info(String.format("Deploying [%s] on context [%s] and port [%s].",
                warFile, context, port));
        GlassFishRuntime runtime = GlassFishRuntime.bootstrap(); // glassfish runtime
        GlassFishProperties prop = new GlassFishProperties();    // glassfish properties
        prop.setPort("http-listener", port);       // app port
        GlassFish gf = runtime.newGlassFish(prop);               // start glassfish server with props
        gf.start();

        //String result = gf.getDeployer().deploy(new File("j2ee/rest/service/target/service-1.0.war"));
        String result = gf.getDeployer().deploy(new File(warFile),
                String.format("--contextroot=%s", context), "--force=true");

        if (result == null) {
            throw new IllegalStateException(String.format("Deployment for [%s] failed", warFile));
        }
    }

}
