package gusev.dmitry.research.locker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Example of LOCK for defense against twice class execution - even from different classloaders/JVMs.
 * I think, its correct... :)
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 03.09.2014)
*/

public class Locker {

    public static void main(String[] args) {
        Log log = LogFactory.getLog(Locker.class);

        String tempDir      = System.getenv("TEMP");
        System.out.println("tmp dir -> " + tempDir);
        String lockFileName = "myprocess.lock";
        File lockFile = Paths.get(tempDir, lockFileName).toFile();
        //FileLock lock = null;
        try (/*FileChannel lockChannel = new RandomAccessFile(lockFile, "rw").getChannel();*/
             FileLock    lock = new RandomAccessFile(lockFile, "rw").getChannel().tryLock()) {

            if (lock != null) {
                log.debug("Lock acquired!");
                // wait for new line from console
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                lock.release();
                scanner.nextLine();
            } else {
                log.debug("Already locked!");
            }

        } catch (IOException e) {
            log.error(e);
        }
    }
}