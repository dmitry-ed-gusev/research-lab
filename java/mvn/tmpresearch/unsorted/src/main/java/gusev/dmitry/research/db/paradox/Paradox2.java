package gusev.dmitry.research.db.paradox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 19.04.13)
 */
public class Paradox2 {

    public static byte[] read(File file) throws IOException {

        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[0x23]; //282-369
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;

            //byte[] zzz = new byte[2];
            int len = ios.read(buffer);
            System.out.println("Read -> " + len);
            //int val = buffer[6] * 256 + buffer[7];

            System.out.println("-> " + new String(buffer, "CP866") + "\n-------------------\n");

            //for (int i = 0; i < buffer.length; i++)
            //    System.out.print((char) buffer[i]);



            System.exit(0);

            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }

    public static void main(String[] args) {
        Log log = LogFactory.getLog(Paradox2.class);

        try {
            byte[] fileBytes = Paradox2.read(new File("c:\\temp\\_zzz\\n00200.db"));
            String str = new String(fileBytes, "Cp866");
            System.out.println(str);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}