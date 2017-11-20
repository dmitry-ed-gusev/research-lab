package gusevdm.deployer;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

import java.io.File;

/**
 * Class deployes WAR archive (application) with GlasssFIsh jee container in embedded mode.
 */
public class AppDeployerGlassfish {

    /***/
    public static void main(String[] args) throws GlassFishException {

        // glassfish runtime
        GlassFishRuntime runtime = GlassFishRuntime.bootstrap();
        // glassfish properties
        GlassFishProperties prop = new GlassFishProperties();
        prop.setPort("http-listener", 8080); // port
        // start glassfish server
        GlassFish gf = runtime.newGlassFish(prop);
        gf.start();

        //String result = gf.getDeployer().deploy(new File("j2ee/rest/service/target/service-1.0.war"));
        String result = gf.getDeployer().deploy(
                new File("nlp\\nlp-webapp\\target\\panalyzer-web.war"),
                "--contextroot=panalyzer");
        if (result == null) {
            throw new IllegalStateException("Deployment failed");
        }
    }

}
