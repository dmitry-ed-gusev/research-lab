package gusev.dmitry.research.net.ssh;

/**
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 17.12.15)
 */
public interface SSHDefaults {

    /***/
    int TIMEOUT_MSEC = 30000;

    /**
     * SUDO prefix for command (for run as a sudo).
     * Info from [man sudo]:
     *  -S  The -S (stdin) option causes sudo to read the password from the standard input
     *  instead of the terminal device.
     *  -p  The -p (prompt) option allows you to override the default password prompt and use a custom one.
     */
    String CMD_SUDO_PREFIX = "sudo -S -p '' ";

    /**
     * Modified SET command for printing variables. By default, SET will output the variables
     * and the functions defines as well. But POSIX mode only outputs the variables.
     */
    String CMD_POSIX_SET = "set -o posix; set";
}
