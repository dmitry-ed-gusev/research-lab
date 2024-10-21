package dgusev.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.apachecommons.CommonsLog;

/** ZIP/GZIP methods */

@CommonsLog
public class MyZippo {

    public static byte[] zip(byte[] source) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zop = new ZipOutputStream(bos);
        ZipEntry ze = new ZipEntry("name");
        zop.putNextEntry(ze);
        zop.write(source);
        zop.closeEntry();
        zop.finish();
        zop.close();
        return bos.toByteArray();
    }

    public static byte[] gzip(byte[] source) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zop = new GZIPOutputStream(bos);
        zop.write(source);
        zop.finish();
        zop.close();
        return bos.toByteArray();
    }

    public static byte[] unzip(byte[] source) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(source);
        ZipInputStream zip = new ZipInputStream(bis);
        zip.getNextEntry();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int read;
        byte[] buf = new byte[1024];
        while ((read = zip.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }
        zip.close();
        return bos.toByteArray();
    }

    public static byte[] ungzip(byte[] source) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(source);
        GZIPInputStream zip = new GZIPInputStream(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int read;
        byte[] buf = new byte[1024];
        while ((read = zip.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }
        zip.close();
        return bos.toByteArray();
    }

    /**
     * Unzip ZIP archive and output its content to outputFolder.
     * If there are files (in output folder) - they will be overwritten.
     *
     * @param zipFile      input zip file
     * @param outputFolder zip file output folder
     */
    public static void unZipIt(String zipFile, String outputFolder) {

        if (StringUtils.isBlank(zipFile)) { // fail-fast
            throw new IllegalArgumentException(String.format("Empty ZIP file name [%s]!", zipFile));
        }

        byte[] buffer = new byte[1024]; // unzip process buffer

        // use try-with-resources for auto close input streams
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {

            if (!StringUtils.isBlank(outputFolder)) {
                //create output directory if not exists
                File folder = new File(outputFolder);
                if (!folder.exists()) {
                    log.info(String.format("Destination output path [%s] doesn't exists! Creating...", outputFolder));
                    if (folder.mkdirs()) {
                        log.info(String.format("Destination output path [%s] created successfully!", outputFolder));
                    } else {
                        throw new IllegalStateException(String.format("Can't create zip output folder [%s]!", outputFolder));
                    }
                }
            } // end of check/create output folder

            ZipEntry ze = zis.getNextEntry(); // get first zip entry and start iteration
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File((StringUtils.isBlank(outputFolder) ? "" : (outputFolder + File.separator)) + fileName);

                log.debug(String.format("Processing -> name: %s | size: %s | compressed size: %s \n\t" +
                                "absolute name: %s",
                        fileName, ze.getSize(), ze.getCompressedSize(), newFile.getAbsoluteFile()));

                // if entry is a directory - create it and continue (skip the rest of cycle body)
                if (fileName.endsWith("/") || fileName.endsWith("\\")) {
                    if (newFile.mkdirs()) {
                        log.debug(String.format("Created dir: [%s].", newFile.getAbsoluteFile()));
                    } else {
                        throw new IllegalStateException(String.format("Can't create dir [%s]!", newFile.getAbsoluteFile()));
                    }
                    ze = zis.getNextEntry();
                    continue;
                }

                // todo: do we need this additional dirs creation?
                //File parent = file.getParentFile();
                //if (parent != null) {
                //    parent.mkdirs();
                //}

                // write extracted file on disk
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            } // end of WHILE cycle

            //zis.closeEntry();
            //zis.close();
            log.info(String.format("Archive [%s] unzipped successfully.", zipFile));

        } catch (IOException ex) {
            log.error(ex);
        }

    } // end of unZipIt

}