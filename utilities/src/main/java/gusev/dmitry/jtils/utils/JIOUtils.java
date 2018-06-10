package gusev.dmitry.jtils.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

public final class JIOUtils {

    private static Log LOG = LogFactory.getLog(JIOUtils.class);

    private JIOUtils() {}

    /** Read simple long value from file (file can be edited with with any editor). */
    public static long readLongFromFile(String filePath) throws IOException {
        LOG.info(String.format("CommonUtils: reading long from file [%s].", filePath));
        // reading from file
        try (FileReader fr = new FileReader(filePath);
             BufferedReader br = new BufferedReader(fr)) {

            // read first line with value
            return Long.parseLong(br.readLine());
        }
    }

    /** Write simple long value to file (file can be edited with with any editor). */
    public static void writeLongToFile(long value, String filePath, boolean overwrite) throws IOException {
        LOG.info(String.format("CommonUtils: write long [%s] to file [%s].", value, filePath));

        // check for file existence (delete if needed)
        File file = new File(filePath);
        if (file.exists() && overwrite) {
            boolean isDeleteOK = file.delete();
            LOG.info(String.format("File [%s] exists. Removing -> [%s].", filePath, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - we won't process.
                LOG.error(String.format("Cant't delete file [%s]!", filePath));
                return;
            }
        }

        // write value to file
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // write value to file
            out.println(String.valueOf(value));
        }
    }

}
