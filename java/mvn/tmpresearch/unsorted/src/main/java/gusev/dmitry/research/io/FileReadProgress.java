package gusev.dmitry.research.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 16.05.13)
 */

public class FileReadProgress {

    public static void main(String[] args) {
        Log log = LogFactory.getLog(FileReadProgress.class);
        log.info("Start.");

        File testFile = new File("research/java24h_lesson9.txt");
        log.info(testFile.exists() + ", size = " + testFile.length() + " byte(s)");

        FileReader     fReader = null;
        BufferedReader bReader = null;
        try {
            fReader = new FileReader("research/java24h_lesson9.txt");
            bReader = new BufferedReader(fReader);
            String line;
            int counter   = 0;
            long fileSize = 0;
            int readPercent = 0;
            while ((line = bReader.readLine()) != null) {
                fileSize += line.length() + 2;
                readPercent = (int) (fileSize*100/testFile.length());
                log.debug(readPercent + "%");
                counter++;
            }
            log.info("Counted size = " + (fileSize) + ", lines = " + counter);

        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

}