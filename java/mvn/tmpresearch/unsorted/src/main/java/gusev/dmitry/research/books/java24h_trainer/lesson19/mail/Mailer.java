package gusev.dmitry.research.books.java24h_trainer.lesson19.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 07.10.12)
 */

public class Mailer {

    // module logger
    private Log log = LogFactory.getLog(Mailer.class);

    // other module fields
    private Session session = null;
    private static String emailSenderAddress = "stratosran@mail.ru";
    private static String emailSubject = "Просто тест мейлера на Джаба :)";
    private String emailText = "Тест для: %s!";
    // friend list, that we will read from text file
    private ArrayList<String> friends = new ArrayList<String>();

    Mailer() {
        Properties sessionProperties = new Properties();
        sessionProperties.put("mail.smtp.host", "smtp.mail.ru");
        sessionProperties.put("mail.smtp.user", emailSenderAddress);
        sessionProperties.put("mail.smtp.port", "587");
        sessionProperties.put("mail.smtp.auth", "true");
        MyMailAuthenticator authentificatorForMessage = new MyMailAuthenticator();
        session = Session.getInstance(sessionProperties, authentificatorForMessage);
    }

    private void setPropsAndSendEmail(String emailRecipient, String emailText) {
        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(emailSenderAddress));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailRecipient, false));
            emailMessage.setSubject(emailSubject);
            emailMessage.setSentDate(new Date());
            emailMessage.setText(emailText);
            Transport.send(emailMessage);
            System.out.println("Your email to " + emailRecipient + " has been sent successfully.");
        } catch (Exception e) {
            System.out.println("Your email to " + emailRecipient + " has not been sent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void readBirthdayFile() throws IOException {
        // getting current dir
        log.debug("current working dir -> " + System.getProperty("user.dir"));
        FileInputStream birthdayFile = new FileInputStream("java24h_lesson9.txt");
        BufferedReader birthdayFileReader = new BufferedReader(new InputStreamReader(birthdayFile));
        String friendInfo;
        while ((friendInfo = birthdayFileReader.readLine()) != null) {
            log.debug("Found line: [" + friendInfo + "].");
            friends.add(friendInfo);
        }
        birthdayFileReader.close();
        birthdayFile.close();
    }

    private void iterateThroughBirthdays() {
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()) {
            this.scanForManInfoAndSendEmail(iterator.next());
        }
    }

    private void scanForManInfoAndSendEmail(String stringFromArray) {
        log.debug("Mailer.scanForManInfoAndSendEmail() working. Input [" + stringFromArray + "].");
        Scanner scannerOfLines = new Scanner(stringFromArray).useDelimiter("[,\n]");
        if (scannerOfLines.next().equals(getCurrentDateMMMd())) {
            String emailAddressee = scannerOfLines.next();
            String emailAddress = scannerOfLines.next();
            log.debug("Trying to send email [address: " + emailAddress + ", addressee: " + emailAddressee + "]. ");
            this.setPropsAndSendEmail(emailAddress, String.format(emailText, emailAddressee));
        }
    }

    private static String getCurrentDateMMMd() {
        return new SimpleDateFormat("MMM-d", Locale.US).format(new GregorianCalendar().getTime());
    }

    public static void main(String[] args) {
        Mailer mm = new Mailer();
        try {
            mm.readBirthdayFile();
            mm.iterateThroughBirthdays();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}