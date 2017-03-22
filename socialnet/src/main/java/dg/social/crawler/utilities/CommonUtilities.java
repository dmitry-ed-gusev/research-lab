package dg.social.crawler.utilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static dg.social.crawler.SCrawlerDefaults.DATE_TIME_FORMAT;
import static dg.social.crawler.utilities.CmdLineOption.OUTPUT_FORCE;

/**
 * Some common utilities.
 * Created by gusevdm on 12/22/2016.
 */

public final class CommonUtilities {

    private static final Log LOG = LogFactory.getLog(CommonUtilities.class); // module logger

    private static final String CUSTOM_PROPERTY_NAME   = "custom_%s";

    private CommonUtilities() {} // can't instantiate

    /**
     * Writes access token and its date from specified file.
     * If file already exist - throw exception or overwrite it (if overwrite = true).
     */
    public static void saveAccessToken(Pair<Date, String> accessToken, String accessTokenFile, boolean overwrite) throws IOException {
        LOG.debug(String.format("CommonUtilities.saveAccessToken() working. Token: [%s], file: [%s], overwrite: [%s].", accessToken, accessTokenFile, overwrite));

        if (accessToken == null || accessToken.getLeft() == null || StringUtils.isBlank(accessToken.getRight()) || StringUtils.isBlank(accessTokenFile)) { // check input parameters
            throw new IllegalArgumentException(
                    String.format("Empty access token (or its part): [%s] or token file name: [%s]!", accessToken, accessTokenFile));
        }

        // check for file existence (delete if needed)
        File file = new File(accessTokenFile);
        if (file.exists() && overwrite) {
            boolean isDeleteOK = file.delete();
            LOG.info(String.format("File [%s] exists. Removing -> [%s].", accessTokenFile, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - we won't process.
                LOG.error(String.format("Cant't delete file [%s]!", accessTokenFile));
                return;
            }
        }

        // write token to file
        try (FileWriter fw = new FileWriter(accessTokenFile);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // write access token and current date/time to file
            out.println(DATE_TIME_FORMAT.format(accessToken.getLeft()));
            out.println(accessToken.getRight());
        }

    }

    /**
     * Reads access token and its date from specified file.
     * If file doesn't exist throw exception.
     */
    public static Pair<Date, String> readAccessToken(String accessTokenFile) throws IOException, ParseException {
        LOG.debug("CommonUtilities.readAccessToken() working.");

        if (StringUtils.isBlank(accessTokenFile)) { // fail-fast
            throw new IllegalArgumentException("File name is null!");
        }

        // reading token from file
        try (FileReader fr = new FileReader(accessTokenFile);
             BufferedReader br = new BufferedReader(fr)) {

            Date   tokenDate = DATE_TIME_FORMAT.parse(br.readLine()); // first line of file
            String token     = br.readLine();                         // second line of file

            return new ImmutablePair<>(tokenDate, token);
        }

    }

    /**
     * Saves string to file with specified or auto-generated file name (based on time).
     * Returns file name.
     * If received string is empty throws run-time exception.
     */
    // todo: thread safety!
    // todo: add file name prefix - to determine source (social network client) for current file
    public static void saveStringToFile(String string, String fileName, boolean overwrite) throws IOException {
        LOG.debug("CommonUtilities.saveStringToFile() working.");

        if (StringUtils.isBlank(string) || StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException(
                    String.format("String to save [%s] and/or file name [%s] is empty!", string, fileName));
        }

        // check for file existence (delete if needed)
        File file = new File(fileName);
        if (file.exists() && overwrite) {
            boolean isDeleteOK = file.delete();
            LOG.info(String.format("File [%s] exists. Removing -> [%s].", fileName, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - we won't process.
                throw new IllegalStateException(String.format("Cant't delete file [%s]!", fileName));
            }
        }

        // write data to file
        try (FileWriter fw = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(string); // write data to file
        }

    }

    /**
     * Unzip ZIP archive and output its content to outputFolder.
     * If there are files (in output folder) - they will be overwritten.
     * @param zipFile input zip file
     * @param outputFolder zip file output folder
     */
    public static void unZipIt(String zipFile, String outputFolder) {

        if (StringUtils.isBlank(zipFile)) { // fail-fast
            throw new IllegalArgumentException(String.format("Empty ZIP file name [%s]!", zipFile));
        }

        byte[] buffer = new byte[1024]; // unzip process buffer

        try {
            if (!StringUtils.isBlank(outputFolder)) {
                //create output directory is not exists
                File folder = new File(outputFolder);
                if (!folder.exists()) {
                    LOG.info(String.format("Destination output path [%s] doesn't exists! Creating...", outputFolder));
                    if (folder.mkdirs()) {
                        LOG.info(String.format("Destination output path [%s] created successfully!", outputFolder));
                    } else {
                        throw new IllegalStateException(String.format("Can't create zip output folder [%s]!", outputFolder));
                    }
                }
            } // end of check/create output folder

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)); //get the zip file content
            ZipEntry       ze  = zis.getNextEntry();                               //get the zipped file list entry
            while(ze != null) {
                String fileName = ze.getName();
                File newFile = new File((StringUtils.isBlank(outputFolder) ? "" : (outputFolder + File.separator)) + fileName);
                LOG.debug(String.format("Unzipping file: absolute path [%s] / short name [%s].",
                        newFile.getAbsoluteFile(), newFile.getName()));

                // todo: create all non exists folders else we hit FileNotFoundException for compressed folder
                // todo: new File(newFile.getParent()).mkdirs();

                // write extracted file on disk
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            } // end of WHILE cycle

            zis.closeEntry();
            zis.close();
            LOG.info(String.format("Archive [%s] unzipped successfully.", zipFile));

        } catch(IOException ex) {
            LOG.error(ex);
        }

    }

    /***/
    public static List<CustomStringProperty> getCustomPropertiesList(CmdLine cmdLine) {
        LOG.debug("SocialCrawler.getCustomProperties() is working [PRIVATE].");

        if (cmdLine == null) {
            throw new IllegalArgumentException("Can't create custom properties from NULL cmd line!");
        }

        List<CustomStringProperty> result = new ArrayList<>();

        // iterate over options and create custom
        // todo: add cmd line option to logger (like: new value for option ....)
        for (CmdLineOption option : CmdLineOption.values()) {

            if (!StringUtils.isBlank(option.getOptionKey())) { // process only config options

                String optionValue        = cmdLine.optionValue(option.getOptionName());
                String customPropertyName = String.format(CUSTOM_PROPERTY_NAME, option.getOptionKey());

                if (!StringUtils.isBlank(optionValue)) { // value isn't empty (present)
                    //LOG.info(String.format(""));
                    LOG.debug(String.format("Creating custom property [%s]: name [%s], value [%s].",
                            customPropertyName, option.getOptionKey(), optionValue));
                    result.add(new CustomStringProperty(customPropertyName, option.getOptionKey(), optionValue));

                } else if (OUTPUT_FORCE.equals(option)) { // one option is a flag

                    if (cmdLine.hasOption(option.getOptionName())) {
                        LOG.debug(String.format("Set value [TRUE] for flag [%s].", option));
                        result.add(new CustomStringProperty(customPropertyName, option.getOptionKey(), String.valueOf(true)));
                    } else {
                        LOG.debug(String.format("There is no new value for flag [%s].", option));
                    }

                } else { // no value and not a flag
                    LOG.debug(String.format("There is no new value for option [%s].", option));
                }

            }

        } // end of FOR

        return result;
    }

}
