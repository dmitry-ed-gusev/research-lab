package gusev.dmitry.research.io.fileman.modules;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 17.01.14)
*/

public class FSZipper {

    private static Log log = LogFactory.getLog(FSZipper.class);

    // object defaults
    private static final int    DEFAULT_COMPRESSION = 9;       // max compression
    private static final String DEFAULT_CHARSET     = "UTF-8"; // UTF-8 character set

    // object internal state
    private int     compressionLevel;
    private Charset filesCharset;

    /***/
    public FSZipper(int compressionLevel, String filesCharset) {
        this.compressionLevel = (compressionLevel >= 0 && compressionLevel <= 9 ? compressionLevel : DEFAULT_COMPRESSION);
        this.filesCharset     = Charset.forName(StringUtils.isBlank(filesCharset) ? DEFAULT_CHARSET : filesCharset);
    }

    /***/
    public void zipFolder(String sourceFolder, String outputZipFile) throws IOException {
        log.debug("FSZipper.zipFolder() working.");
        FileOutputStream fos = new FileOutputStream(outputZipFile);
        ZipOutputStream zos = new ZipOutputStream(fos, this.filesCharset);
        zos.setLevel(this.compressionLevel); // the compression level (0-9)
        log.info("Compressing folder : " + sourceFolder + " to " + outputZipFile);
        FSZipper.addFolder(zos, sourceFolder, sourceFolder);
        zos.close();
        log.debug("Zipping finished.");
    }

    /***/
    private static void addFolder(ZipOutputStream zos, String folderName, String baseFolderName) throws IOException {
        File file = new File(folderName);
        if (file.exists()) { // processing, if file/folder exists
            if (file.isDirectory()) { // add a directory
                File subFolderFiles[] = file.listFiles();
                if (subFolderFiles != null) {
                    for (File subFolderFile : subFolderFiles) {
                        FSZipper.addFolder(zos, subFolderFile.getAbsolutePath(), baseFolderName);
                    }
                }
            } else { //add a file
                //extract the relative name for entry purpose
                String entryName = folderName.substring(baseFolderName.length() + 1, folderName.length());
                log.debug("Adding entry [" + entryName + "].");
                ZipEntry ze = new ZipEntry(entryName);
                // adding entry info
                try (FileInputStream in = new FileInputStream(folderName)) {
                    zos.putNextEntry(ze);
                    int len;
                    byte buffer[] = new byte[1024];
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    in.close();
                } finally {
                    zos.closeEntry();
                }
            }
        } else { // file/folder doesn't exist
            log.warn("File or directory not found " + folderName);
        }
    }

}