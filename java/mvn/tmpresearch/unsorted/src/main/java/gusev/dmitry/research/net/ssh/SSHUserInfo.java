package gusev.dmitry.research.net.ssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;

/**
 * Prepared answers (callback methods) for system questions (via SSH session).
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 18.12.2015)
 */
public class SSHUserInfo implements UserInfo, UIKeyboardInteractive {

    private static final Log log = LogFactory.getLog(SSHUserInfo.class);
    private String password;

    /***/
    public SSHUserInfo(String password) {
        this.password = password;
    }

    /**
     * Returns password when called.
     * @return String ssh password.
     */
    public String getPassword() {
        log.debug("SessionUserInfo.getPassword().");
        return this.password;
    }

    /**
     * Answer for system prompts (Yes/No).
     * @param message String prompt text.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean promptYesNo(String message) {
        boolean answer = true;
        log.debug(String.format("SessionUserInfo.promptYesNo().%nMessage: [%s].%nAnswer: %s", message, answer ? "yes" : "no"));
        return answer; // always answer YES
    }

    /***/
    public String getPassphrase() {
        log.debug("SessionUserInfo.getPassphrase().");
        return null;
    }

    /***/
    public boolean promptPassphrase(String message) {
        log.debug("SessionUserInfo.promptPassphrase().");
        return true;
    }

    /**
     * Method accepts (returns true) or cancel (returns false) auth by returning boolean value.
     * If returned true, next call is to method getPassword().
     *
     * @param message String remote host message for password prompt.
     * @return boolean accept (true)/cancel (false) remote auth.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean promptPassword(String message) {
        boolean answer = true;
        log.debug(String.format("SessionUserInfo.promptPassword().%nMessage: [%s].%nAnswer: %s", message, answer ? "accept auth" : "cancel auth"));
        return answer; // always accept auth
    }

    /***/
    public void showMessage(String message) {
        log.debug("SessionUserInfo.showMessage().");
    }

    /***/
    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
        log.debug("SessionUserInfo.promptKeyboardInteractive().");
        log.debug(String.format("destination = %s, name = %s, instruction = %s, prompt = %s, echo = %s",
                destination, name, instruction, Arrays.toString(prompt), Arrays.toString(echo)));
        return null;  // always cancel prompt
    }

}