package dg.social.utilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.ParseException;
import java.util.Date;

import static dg.social.CommonDefaults.DATE_TIME_FORMAT;

/**
 * Some common utilities.
 * Created by gusevdm on 12/22/2016.
 */

public final class CommonUtilities {

    private static final Log LOG = LogFactory.getLog(CommonUtilities.class); // module logger

    private CommonUtilities() {} // can't instantiate

    /**
     * Writes access token and its date from specified file.
     * If file already exist - throw exception or overwrite it (if overwrite = true).
     */
    public static void saveAccessToken(Pair<Date, String> accessToken, String accessTokenFile, boolean overwrite) throws IOException {
        LOG.debug(String.format("VkClient.saveAccessToken() working. Token: [%s], file: [%s], overwrite: [%s].", accessToken, accessTokenFile, overwrite));

        if (accessToken == null || accessToken.getLeft() == null || StringUtils.isBlank(accessToken.getRight()) || StringUtils.isBlank(accessTokenFile)) { // check input parameters
            throw new IllegalArgumentException(
                    String.format("Empty token date/time [%s],  access token [%s] or file name [%s]!", accessToken.getLeft(), accessToken.getRight(), accessTokenFile));
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
        LOG.debug("VkClient.readAccessToken() working.");

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

}
