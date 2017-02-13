package dg.social.crawler;

import dg.social.crawler.domain.Person;
import dg.social.crawler.ok.OkClient;
import dg.social.crawler.ok.OkClientConfig;
import dg.social.crawler.ok.OkFormsRecognizer;
import dg.social.crawler.parsing.VkParser;
import dg.social.crawler.utilities.CmdLine;
import dg.social.crawler.utilities.CmdLineOption;
import dg.social.crawler.utilities.CommonUtilities;
import dg.social.crawler.vk.VkClient;
import dg.social.crawler.vk.VkClientConfig;
import dg.social.crawler.vk.VkFormsRecognizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static dg.social.crawler.utilities.CmdLineOption.*;

/**
 * Crawler for social networks. This class uses social networks clients for mining info
 * from various networks.
 * Created by gusevdm on 12/27/2016.
 */

// todo: extract parse cmd line method
// todo: move functionality to SocialCrawler instance

public class SocialCrawler {

    private static final String LOGGER_ROOT = "dg.social";
    private static final Log    LOG         = LogFactory.getLog(SocialCrawler.class);

    //private CmdLine cmdLine;
    private String  configFile;
    private String  searchString;
    private String  outputFile;
    private boolean forceOutput;


    /***/
    public SocialCrawler(CmdLine cmdLine) {

        if (cmdLine == null) {
            throw new IllegalArgumentException("Can't work with empty cmd line!");
        }

        //this.cmdLine = cmdLine;
    }

    /***/
    public static void main(String[] args) {
        LOG.info("SocialCrawler starting.");

        CmdLine cmdLine = new CmdLine(args, false); // parse cmd line and disable internal logging

        // if here is -help option - show usage text and exit
        if (cmdLine.hasOption(CmdLineOption.HELP.getOptionName())) {
            System.out.println(CmdLineOption.getHelpText()); // show help/usage text
            System.exit(0); // force exit with code 0 (all OK)
        }

        // get logger level option (for override, if needed)
        String logLevel = cmdLine.optionValue(LOGGER_LEVEL.getOptionName());
        if (!StringUtils.isBlank(logLevel)) {
            LogManager.getLogger(LOGGER_ROOT).setLevel(Level.toLevel(logLevel.toUpperCase()));
        }
        // just debug - cmd line output
        LOG.debug(String.format("Received command line: %s.", (args != null ? Arrays.toString(args) : "null")));


        // get config file value
        String configFile = cmdLine.optionValue(CONFIG_FILE.getOptionName());
        if (StringUtils.isBlank(configFile)) { // fail-fast -> can't work without config
            LOG.error("You have to specify config file with option: [-config <filename>].");
            System.out.println(CmdLineOption.getHelpText()); // show help/usage text
            System.exit(1);
        }

        // get search string value
        String searchString = cmdLine.optionValue(SEARCH_STRING.getOptionName());
        if (StringUtils.isBlank(searchString)) { // fail-fast -> can't work with empty search string
            LOG.error("Can't search by empty search string!");
            System.out.println(CmdLineOption.getHelpText()); // show help/usage text
            System.exit(1);
        }

        // get output file value and force option
        boolean forceOutput = cmdLine.hasOption(OUTPUT_FORCE.getOptionName());
        String  outputFile  = cmdLine.optionValue(OUTPUT_FILE.getOptionName());
        if (StringUtils.isBlank(outputFile)) { // fail-fast -> can't work with empty output file
            LOG.error("Can't output to empty file!");
            System.out.println(CmdLineOption.getHelpText()); // show help/usage text
            System.exit(1);
        }

        // Config file option isn't empty - go ahead
        try (FileReader fr = new FileReader(configFile);
             BufferedReader br = new BufferedReader(fr)) {

            // load properties from config file
            Properties properties = new Properties();
            properties.load(br);
            LOG.debug(String.format("Properties from [%s] file: %s.", configFile, properties));

            if (true) { // unzip data fil from Telescope system

                CommonUtilities.unZipIt("people.zip", "");
                System.exit(777);
            }


            if (true) { // load VK client and search
                // create vk client config
                VkClientConfig vkClientConfig = new VkClientConfig(properties);
                // create vk client
                VkClient vkClient = new VkClient(vkClientConfig, new VkFormsRecognizer());

                // search for simple search string
                String jsonResult = vkClient.usersSearch(searchString,
                        "about,activities,bdate,books,career,city,contacts,country,education,exports,games," +
                                "home_town,interests,home_town,maiden_name,movies,music,nickname,occupation,personal,quotes," +
                                "relatives,relation,schools,sex,site,status,tv,universities", 1000);
                //System.out.println("-> " + jsonResult);

                // parse search results
                List<Person> users = VkParser.parseUsers(jsonResult);
                //System.out.println("-> " + users);

                if (!users.isEmpty()) { // save only if there is anything
                    // save search results to file
                    StringBuilder builder = new StringBuilder();
                    for (Person person : users) {
                        builder.append(person.toString()).append("\n");
                    }
                    CommonUtilities.saveStringToFile(builder.toString(), outputFile, forceOutput);
                }

            }

            if (false) { // load OK client and search
                // create ok client config
                OkClientConfig okClientConfig = new OkClientConfig(properties);
                // create ok client
                OkClient okClient = new OkClient(okClientConfig, new OkFormsRecognizer());
            }

        } catch (IOException | ParseException | URISyntaxException e) {
            LOG.error(e);
            // e.printStackTrace(); // <- for deep debug
        }
    }

}
