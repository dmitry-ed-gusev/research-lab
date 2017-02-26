package dg.social.crawler;

import dg.social.crawler.components.VkComponent;
import dg.social.crawler.utilities.CmdLine;
import dg.social.crawler.utilities.CmdLineOption;
import dg.social.crawler.utilities.CommonUtilities;
import dg.social.crawler.utilities.CustomSpringProperty;
import dg.social.crawler.networks.telescope.TelescopeParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.hibernate.SessionFactory;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dg.social.crawler.utilities.CmdLineOption.*;

/**
 * Crawler for social networks. This class uses social networks clients for mining info
 * from various networks.
 * Created by gusevdm on 12/27/2016.
 */

// todo: extract parse cmd line utility method/class
// todo: move functionality to SocialCrawler instance

@Component
@Transactional
public class SocialCrawler {

    private static final Log LOG = LogFactory.getLog(SocialCrawler.class);

    private static final String LOGGER_ROOT = "dg.social";
    private static final String SPRING_CONFIG = "CrawlerSpringContext.xml";
    private static final String CRAWLER_DEFAULT_CONFIG = "crawler.default.config";
    private static final String zzz = "";

    //private CmdLine cmdLine;
    //private String configFile;
    //private String searchString;
    //private String outputFile;
    //private boolean forceOutput;

    @Autowired @Qualifier(value = "crawlerHsqlSessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    private VkComponent vkComponent;

    /***/
    public SocialCrawler() {

        //if (cmdLine == null) {
        //    throw new IllegalArgumentException("Can't work with empty cmd line!");
        // }

        //this.cmdLine = cmdLine;
    }

    /***/
    private static List<CustomSpringProperty> getCustomProperties(CmdLine cmdLine) {
        LOG.debug("SocialCrawler.getCustomProperties() is working.");
        List<CustomSpringProperty> result = new ArrayList<>();

        // get new config file value and create custom property
        String configFile = cmdLine.optionValue(CONFIG_FILE.getOptionName());
        if (!StringUtils.isBlank(configFile)) {
            LOG.debug(""); // todo !!!
            result.add(new CustomSpringProperty("custom_config",
                    CRAWLER_DEFAULT_CONFIG, configFile));
        }


        return result;
    }

    /***/
    public void start() throws ParseException, IOException, URISyntaxException {
        LOG.debug("SocialCrawler.start() is working.");
        this.vkComponent.updateCountries();

        //this.sessionFactory.close();
        LOG.info("Finishing SocialCrawler...");
        this.sessionFactory.getCurrentSession().createSQLQuery("CHECKPOINT").executeUpdate();
        this.sessionFactory.getCurrentSession().createSQLQuery("SHUTDOWN").executeUpdate();
    }

    /***/
    public static void main(String[] args) {
        LOG.info(String.format("SocialCrawler starting. Command line: [%s].", Arrays.toString(args)));

        // init CmdLine instance
        CmdLine cmdLine = new CmdLine(args); // parse cmd line and disable internal logging

        // if here is -help option - show usage text and exit
        if (cmdLine.hasOption(CmdLineOption.HELP.getOptionName())) {
            System.out.println(CmdLineOption.getHelpText()); // show help/usage text
            System.exit(0); // force exit with code 0 (all OK)
        }

        // get logger level option (for override, if needed)
        String logLevel = cmdLine.optionValue(LOGGER_LEVEL.getOptionName());
        if (!StringUtils.isBlank(logLevel)) {
            LogManager.getLogger(LOGGER_ROOT).setLevel(Level.toLevel(logLevel.toUpperCase()));
            LOG.info(String.format("Set logging level to [%s].", logLevel));
        }

        // create list of custom properties for Spring container
        List<CustomSpringProperty> customProperties = SocialCrawler.getCustomProperties(cmdLine);

        // get search string value
        String searchString = cmdLine.optionValue(SEARCH_STRING.getOptionName());
        if (StringUtils.isBlank(searchString)) { // fail-fast -> can't work with empty search string
            LOG.error("Can't search by empty search string!");
            System.out.println(CmdLineOption.getHelpText()); // show help/usage text
            System.exit(1);
        }

        // get output file value and force option
        boolean forceOutput = cmdLine.hasOption(OUTPUT_FORCE.getOptionName());
        String outputFile = cmdLine.optionValue(OUTPUT_FILE.getOptionName());
        if (StringUtils.isBlank(outputFile)) { // fail-fast -> can't work with empty output file
            LOG.error("Can't output to empty file!");
            System.out.println(CmdLineOption.getHelpText()); // show help/usage text
            System.exit(1);
        }

        try {

            // initialize Spring application context
            AbstractApplicationContext crawlerContext = new ClassPathXmlApplicationContext(
                    new String[]{SPRING_CONFIG}, false);

            if (!customProperties.isEmpty()) {
                LOG.debug(""); // todo !!!
                MutablePropertySources propertySources = crawlerContext.getEnvironment().getPropertySources();
                for (CustomSpringProperty property : customProperties) {
                    propertySources.addLast(property);
                }
            }

            // load Spring application context
            crawlerContext.refresh();

            // get SocialCrawler instance for Spring context and start it
            SocialCrawler crawler = (SocialCrawler) crawlerContext.getBean("socialCrawler");
            crawler.start();

            if (false) { // unzip data fil from Telescope system

                CommonUtilities.unZipIt("people.zip", "");
                System.exit(777);
            }


            if (false) { // load VK client and search
                // create vk client config
                //VkClientConfig vkClientConfig = new VkClientConfig(properties);
                // create vk client
                //VkClient vkClient = new VkClient(vkClientConfig, new VkFormsRecognizer());

                /*
                String jsonResult = vkClient.getCountries();
                //System.out.println("-> " + jsonResult);
                // parse search results
                List<CountryDto> countries = VkParser.parseCountries(jsonResult);
                System.out.println("countries -> " + countries);

                CountriesDao countriesDao = (CountriesDao) crawlerContext.getBean("countriesDao");

                for (CountryDto country : countries) {
                    countriesDao.addCountry(country);
                }
*/

                /*
        if (!users.isEmpty()) { // save only if there is anything
            // save search results to file
            StringBuilder builder = new StringBuilder();
            for (PersonDto person : users) {
                builder.append(person.toString()).append("\n");
            }
            CommonUtilities.saveStringToFile(builder.toString(), outputFile, forceOutput);
        }

                */

            }

        } catch (Exception e) { // catch any possible exception and log it
            LOG.error(e);
            //e.printStackTrace(); // <- for deep debug
        }
    }

}
