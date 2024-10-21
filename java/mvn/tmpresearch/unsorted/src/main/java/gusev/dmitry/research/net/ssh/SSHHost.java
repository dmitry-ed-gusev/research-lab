package gusev.dmitry.research.net.ssh;

import com.jcraft.jsch.JSchException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class represents simple net host.
 * Class is immutable.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 16.12.2015)
*/

// todo: write tests!
public final class SSHHost {

    private static final Log log = LogFactory.getLog(SSHHost.class);

    private final String name;          // not null, mandatory
    private final String user;
    private final String pass;
    private final String sudoPass;
    private final String host;          // not null, mandatory
    private final int    port;          // if specified <= 0 then default = 22
    private final String description;

    /**
     * @param name String name for this net host, mandatory, not null!
     * @param user String user for SSH connection, optional
     * @param pass String password for SSH connection, optional
     * @param sudoPass String sudo password for run commands with root rights, optional
     * @param host String host for SSH connection, mandatory, not null!
     * @param port int port for SSH connection, if <= 0, then port = 22 will be used
     * @param description String description (additional info) for host, optional
     */
    public SSHHost(String name, String user, String pass, String sudoPass, String host, int port, String description) throws JSchException {
        log.debug("SSHHost.constructor() working.");

        if (StringUtils.isBlank(name) || StringUtils.isBlank(host)) { // check state - fail if incorrect
            throw new IllegalArgumentException("Host name/address shouldn't be empty!");
        }
        // init internal state. if provided null values - set empty strings ("")
        this.name        = StringUtils.trimToEmpty(name);
        this.user        = StringUtils.trimToEmpty(user);
        this.pass        = StringUtils.trimToEmpty(pass);
        this.sudoPass    = StringUtils.trimToEmpty(sudoPass);
        this.host        = StringUtils.trimToEmpty(host);
        this.port        = (port <= 0 ? 22 : port); // by default use port #22
        this.description = StringUtils.trimToEmpty(description);
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getSudoPass() {
        return sudoPass;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Description value ignored.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SSHHost sshHost = (SSHHost) o;

        if (port != sshHost.port)               return false;
        if (!name.equals(sshHost.name))         return false;
        if (!user.equals(sshHost.user))         return false;
        if (!pass.equals(sshHost.pass))         return false;
        if (!sudoPass.equals(sshHost.sudoPass)) return false;
        return host.equals(sshHost.host);
    }

    /**
     * Description value ignored.
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + pass.hashCode();
        result = 31 * result + sudoPass.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("user", user)
                .append("pass", pass)
                .append("sudoPass", sudoPass)
                .append("host", host)
                .append("port", port)
                .append("description", description)
                .toString();
    }

}