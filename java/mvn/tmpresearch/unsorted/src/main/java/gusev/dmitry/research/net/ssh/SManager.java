package gusev.dmitry.research.net.ssh;

import com.jcraft.jsch.JSchException;
import gusev.dmitry.research.net.ssh.gui.SManagerGui;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static gusev.dmitry.research.net.ssh.SSHDefaults.CMD_POSIX_SET;

/**
 * SManager standalone app main class.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 15.12.2015)
 */

public class SManager {

    // module logger
    private static final Log log = LogFactory.getLog(SManager.class);
    // spring context
    private static final String SPRING_CONFIG_NAME = "spring/SSHContext.xml";

    /***/
    public static void main(String[] args) {
        log.info("SManager starting...");

        // processing command line arguments
        //CommandLine cmdLine = new CommandLine(args); // read command line
        try {
            // load spring context with changed property value
            AbstractApplicationContext context = new ClassPathXmlApplicationContext(new String[]{SPRING_CONFIG_NAME}, false);
            // add custom property from cmd line to context
            //context.getEnvironment().getPropertySources().addLast(new CustomPropertySource("custom", CUSTOM_PROPERTY, value));
            context.refresh(); // refresh context (load it completely)
            log.debug("Application context initialized and loaded.");

            // get list of hosts
            Set<SSHHost> hosts = (Set) context.getBean("hosts");

            // init GUI
            SManagerGui smanagerPanel = new SManagerGui(hosts);

            // check status for all servers
            /*
            Pair<Integer, String> result = null;
            for (Map.Entry<String, SSHHost> entry : hosts.entrySet()) {
                try {
                    result = SSHEngine.execute(entry.getValue(), CMD_POSIX_SET, false);
                } catch (JSchException | IOException e) {
                    log.error(String.format("Error execute [%s] on host [%s:%s]. Error message [%s].",
                            CMD_POSIX_SET, entry.getValue().getHost(), entry.getValue().getPort(), result == null ? "" : result.getValue()));
                }

            }
*/

        } catch (BeansException e) { // context loading exception
            log.error(String.format("Can't load context from [%s]!", SPRING_CONFIG_NAME), e);
        }

        //
        log.info("Servers Manager MAIN THREAD finished.");
    }

}