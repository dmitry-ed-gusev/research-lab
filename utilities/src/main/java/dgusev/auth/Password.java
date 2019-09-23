package dgusev.auth;

import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Password storage class (from ancient time :)).
 *
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 10.03.2009)
 */

@CommonsLog
public class Password implements Serializable {

    private final static int XOR_MODULE = 13; // base module vbalue for XOR function

    private byte[] bytePassword; // password storage, we store pass after XOR source byte-by-byte

    /***/
    public Password(@NonNull String password) {
        LOG.debug("Password constructor() is working.");
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Provided empty source password!");
        }

        byte[] foreignPass = password.getBytes();
        bytePassword = new byte[foreignPass.length];
        // Побайтно ксорим (XOR) исходный пароль по нашему модулю и полученные значения сохраняем
        for (int i = 0; i < foreignPass.length; i++) {
            bytePassword[i] = (byte) (foreignPass[i] ^ XOR_MODULE);
        }
    }

    public String getPassword() {
        byte[] byteResult = new byte[bytePassword.length];
        // Для превращения заксоренного пароля в обычный вид - снова ксорим его по нашему модулю
        for (int i = 0; i < bytePassword.length; i++) {
            byteResult[i] = (byte) (bytePassword[i] ^ XOR_MODULE);
        }
        return new String(byteResult);
    }

}