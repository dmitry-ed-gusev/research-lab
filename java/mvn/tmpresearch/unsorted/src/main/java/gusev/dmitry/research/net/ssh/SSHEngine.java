package gusev.dmitry.research.net.ssh;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static gusev.dmitry.research.net.ssh.SSHDefaults.CMD_SUDO_PREFIX;
import static gusev.dmitry.research.net.ssh.SSHDefaults.TIMEOUT_MSEC;

/**
 * Engine for execute command on a remote host over SSH.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 16.12.2015)
 */

// todo: implement multithreaded batch executor

public class SSHEngine {

    private static final Log log = LogFactory.getLog(SSHEngine.class);

    // buffer for read ssh channel output (bytes)
    private static final int SSH_BUFFER           = 1024;
    // wait for XXX msec for ssh session/channel output
    private static final int SSH_OUTPUT_READ_WAIT = 1000;

    /**
     * @return Pair[Integer, String]
     */
    public static Pair<Integer, String> execute(SSHHost host, String command, boolean asRoot) throws JSchException, IOException {
        log.debug("SSHEngine.execute() working.");

        if (host == null || StringUtils.isBlank(command)) { // check input data
            throw new IllegalArgumentException(String.format("Empty host [%s] or command [%s]!", String.valueOf(host), command));
        }

        // init ssh session
        JSch jshell = new JSch();                                                                  // create JSCH instance
        Session jshellSession = jshell.getSession(host.getUser(), host.getHost(), host.getPort()); // create ssh session
        jshellSession.setPassword(host.getPass());                                                 // set user password
        jshellSession.setUserInfo(new SSHUserInfo(host.getPass()));                                // set UserInfo for session

        // this parameter and value is used for testing - to avoid host key checking
        jshellSession.setConfig("StrictHostKeyChecking", "no");

        //jshellSession.setConfig("PreferredAuthentications", "publickey,keyboard-interactive");

        jshellSession.connect(TIMEOUT_MSEC); // connect with timeout
        log.debug(String.format("Connected to [%s:%s].", host.getHost(), host.getPort()));

        Channel jschChannel = jshellSession.openChannel("exec"); // open channel for exec
        ((ChannelExec) jschChannel).setCommand(asRoot ? CMD_SUDO_PREFIX + command : command);         // set command to execute

        // set input for exec channel (null -> no input for command execution)
        jschChannel.setInputStream(null);
        // get errors from channel
        ByteArrayOutputStream errOutput = new ByteArrayOutputStream();
        ((ChannelExec) jschChannel).setErrStream(errOutput);
        // get normal output from channel (into input stream)
        InputStream in = jschChannel.getInputStream();
        // get output stream for write to channel
        OutputStream out = jschChannel.getOutputStream();

        jschChannel.connect(); // execute the command via ssh

        if (asRoot) { // execute command as root (using sudo command)
            log.debug("Execute command as root. Using user provided sudo password.");
            out.write((host.getSudoPass() + "\n").getBytes());
            out.flush();
        }
        log.debug(String.format("Command [%s] executed%s.", command, (asRoot ? " as root (sudo)" : "")));

        // get execution result
        byte[] buffer = new byte[SSH_BUFFER];
        StringBuilder output = new StringBuilder();
        int status; // normal exit status
        while (true) {
            while (in.available() > 0) { // read input
                int i = in.read(buffer, 0, SSH_BUFFER);
                if (i < 0) { // no more data for read, break read cycle
                    break;
                }
                output.append(new String(buffer, 0, i)); // append readed data to output
            }
            if (jschChannel.isClosed()) { // channel closed
                if (in.available() > 0) { // if there are more data - next iteration for outer loop -> while(true)
                    continue;
                }
                status = jschChannel.getExitStatus(); // get command exit status
                break;
            }
            try { // wait 1 sec before next iteration (maybe new data will appear)
                Thread.sleep(SSH_OUTPUT_READ_WAIT);
            } catch (InterruptedException e) {
                log.error(String.format("Interrupted! Message [%s].", e.getMessage()));
            }
        }

        // debug output - execution result (too much output - uncomment only if necessary)
        //log.debug(String.format("%nExecution [%s] result: %nexit status = [%s]; %nnormal output = [%n%s]; %nerror output = [%n%s]",
        //        command, status, output.toString(), errOutput.toString()));

        // generate execution result
        Pair<Integer, String> execResult = (status == 0 ? new ImmutablePair<>(status, output.toString()) : new ImmutablePair<>(status, errOutput.toString()));

        // disconnect after execution
        jschChannel.disconnect();
        jshellSession.disconnect();

        return execResult;
    }

    /**
     * Executes command with superuser (root) rights using sudo command.
     */
    public static Pair<Integer, String> executeAsRoot(SSHHost host, String command) throws IOException, JSchException {
        log.debug("SSHEngine.executeAsRoot() working.");
        return SSHEngine.execute(host, command, true);
    }

    /***/
    public static Map<SSHHost, Pair<Integer, String>> batchExecute(Set<SSHHost>hosts, String command, boolean asRoot) {
        log.debug("SSHEngine.batchExecute() working.");

        if (hosts == null || hosts.isEmpty() || StringUtils.isBlank(command)) { // check input data
            throw new IllegalArgumentException(String.format("Empty hosts list [%s] or command [%s]!",
                    (hosts == null ? "null" : "size=0"), command));
        }

        Map<SSHHost, Pair<Integer, String>> result = new HashMap<>(); // execution result

        // iterate over hosts list and execute command (single thread)
        for (SSHHost host : hosts) {
            //try {
            //    result.put(host, SSHEngine.execute(host, command, asRoot));
            //}
        }

        return result;
    }

    /***/
    public static Map<SSHHost, Pair<Integer, String>> batchExecuteAsRoot(Set<SSHHost>hosts, String command) {
        log.debug("SSHEngine.batchExecuteAsRoot() working.");
        return SSHEngine.batchExecute(hosts, command, true);
    }

    /***/
    public static void scpGetFile(SSHHost host, String fileName) {
        log.debug("SSHEngine.scpGetFile() working.");
    }

    /***/
    public static void scpPutFile(SSHHost host, String fileName) {
        log.debug("SSHEngine.scpPutFile() working.");
    }

}