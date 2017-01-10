package dg.social;

import dg.social.domain.VkUser;
import dg.social.ok.OkClient;
import dg.social.ok.OkClientConfig;
import dg.social.parsing.VkParser;
import dg.social.utilities.CmdLine;
import dg.social.vk.VkClient;
import dg.social.vk.VkClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import static dg.social.utilities.CmdLineOption.CONFIG_FILE;

/**
 * Crawler for social networks. This class uses social networks clients for mining info
 * from various networks.
 * Created by gusevdm on 12/27/2016.
 */

public class SocialCrawler {

    private static final Log LOG = LogFactory.getLog(SocialCrawler.class);

    /***/
    public static void main(String[] args) {
        LOG.debug("SocialCrawler starting...");

        CmdLine cmdLine = new CmdLine(args); // parse cmd line

        String configFile = cmdLine.optionValue(CONFIG_FILE.getOptionName());
        if (StringUtils.isBlank(configFile)) {
            LOG.error("You have to specify config file with option: [-config <filename>].");
            System.exit(1);
        }

        // Config file option isn't empty - go ahead
        try (FileReader fr = new FileReader(configFile);
             BufferedReader br = new BufferedReader(fr)) {

            // load properties from config file
            Properties properties = new Properties();
            properties.load(br);
            LOG.debug(String.format("Properties from [%s] file: %s.", configFile, properties));

            /*
            // create vk client config
            VkClientConfig vkClientConfig = new VkClientConfig(properties);
            // create vk client
            VkClient vkClient = new VkClient(vkClientConfig);
            // search and parse results
            String jsonResult = vkClient.usersSearch("Гусев Дмитрий",
                    "about,activities,bdate,books,career,city,contacts,country,education,exports,games," +
                            "home_town,interests,home_town,maiden_name,movies,music,nickname,occupation,personal,quotes," +
                            "relatives,relation,schools,sex,site,status,tv,universities", 1000);
            //System.out.println("-> " + jsonResult);
            List<VkUser> users = VkParser.parseUsers(jsonResult);
            System.out.println("-> " + users);
            */

            // create ok client config
            OkClientConfig okClientConfig = new OkClientConfig(properties);
            // create ok client
            OkClient okClient = new OkClient(okClientConfig);


        } catch (IOException /*| ParseException | URISyntaxException*/ e) {
            LOG.error(e);
            // e.printStackTrace(); // <- for deep debug
        }
    }

}
