package gusev.dmitry.jtils.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 16.01.14, first version: 2011, RMRS)
 */

public class CRCCode {

    //private static Log log = LogFactory.getLog(CRCCode.class);

    /**
     * Метод возвращает контрольную сумму CRC32 для файла fileName. Если файла не существует или подсчет не
     * удался (возникла ИС), метод возвращает значение 0.
     *
     * @param file File file for CRCr calculating
     * @return long value of CRC32.
    */
    public static long getCRC(File file) throws IOException {
        if (file == null) { // check file object
            throw new IOException("Empty file name!");
        }
        CRC32 crc;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            crc = new CRC32();
            int iByte;
            while ((iByte = in.read()) != -1) { // cycle for calculating
                crc.update(iByte);
            }
            //log.debug("CalcCRC: file [" + file + "]; result [" + result + "]");
        }
        return crc.getValue();
    }

}