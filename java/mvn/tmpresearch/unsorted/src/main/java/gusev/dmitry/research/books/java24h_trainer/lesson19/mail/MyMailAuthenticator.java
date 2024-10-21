package gusev.dmitry.research.books.java24h_trainer.lesson19.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 07.10.12)
 */

public class MyMailAuthenticator extends Authenticator {

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("stratosran@mail.ru", "gusevd");
    }
}