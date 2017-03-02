package dg.social.crawler;

import dg.social.crawler.components.VkComponent;
import dg.social.crawler.utilities.CmdLine;
import dg.social.crawler.utilities.CmdLineOption;
import dg.social.crawler.utilities.CommonUtilities;
import dg.social.crawler.utilities.CustomStringProperty;
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
public class SCrawler {

    private static final Log LOG = LogFactory.getLog(SCrawler.class);

    private static final String LOGGER_ROOT            = "dg.social";
    private static final String SPRING_CONFIG          = "CrawlerSpringContext.xml";
    private static final String CUSTOM_PROPERTY_NAME   = "custom_%s";

    // Configuration properties. Should be the same in crawler config file and in Spring config.
    // todo: move to CmdLineOptions enum
    //private static final String CRAWLER_DEFAULT_DB_PATH       = "crawler.db.path";
    //private static final String CRAWLER_DEFAULT_CONFIG        = "crawler.default.config";
    //private static final String CRAWLER_DEFAULT_SEARCH_STRING = "crawler.default.search.string";
    //private static final String CRAWLER_DEFAULT_OUTPUT_FILE   = "crawler.default.output.file";
    //private static final String CRAWLER_DEFAULT_OUTPUT_FORCE  = "crawler.default.output.force";

    @Autowired
    private SCrawlerConfig crawlerСonfig;
    @Autowired @Qualifier(value = "crawlerHsqlSessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    private VkComponent    vkComponent;

    /***/
    public SCrawler() {
    }

    /***/
    private static CustomStringProperty createCustomProperty(String name, String value) {
        LOG.debug("SCrawler.createCustomProperty() is working [PRIVATE].");
        CustomStringProperty property = null;

        if (!StringUtils.isBlank(name) && !StringUtils.isBlank(value)) {
            String customPropertyName =

            property = new CustomStringProperty(customPropertyName, name, value);
        } else {
            LOG.debug(String.format("Custom property name [%s] and/or value [%s] empty.", name, value));
        }

        return property;
    }

    /***/
    private static List<CustomStringProperty> getCustomProperties(CmdLine cmdLine) {
        LOG.debug("SocialCrawler.getCustomProperties() is working [PRIVATE].");
        List<CustomStringProperty> result = new ArrayList<>();

        // iterate over options and create custom
        for (CmdLineOption option : CmdLineOption.values()) {

            if (!StringUtils.isBlank(option.getOptionKey())) { // process only config options
                String optionValue = cmdLine.optionValue(option.getOptionName());

                if (!StringUtils.isBlank(optionValue)) { // value isn't empty (present)
                    String customPropertyName = String.format(CUSTOM_PROPERTY_NAME, option.getOptionKey());
                    LOG.debug(String.format("Creating custom property [%s]: name [%s], value [%s].",
                            customPropertyName, option.getOptionKey(), optionValue));
                    result.add(new CustomStringProperty(customPropertyName, option.getOptionKey(), optionValue));
                } else if (OUTPUT_FORCE.equals(option)) { // one option is a flag
                    LOG.debug(String.format("Set value for flag [%s].", option));
                    result.add(new CustomStringProperty(String.format(CUSTOM_PROPERTY_NAME, option.getOptionKey()), ))
                } else { // no value and not a flag
                    LOG.debug(String.format("No value for option [%s].", option));
                }

            }

        } // end of FOR

        // custom path to DB
        CustomStringProperty property = SCrawler.createCustomProperty(
                CRAWLER_DEFAULT_DB_PATH, cmdLine.optionValue(DB_PATH.getOptionName()));
        if (property != null) {
            result.add(property);
        }

        // custom crawler config
        property = SCrawler.createCustomProperty(
                CRAWLER_DEFAULT_CONFIG, cmdLine.optionValue(CONFIG_FILE.getOptionName()));
        if (property != null) {
            result.add(property);
        }

        // get search string for 'quick search' (applied option)
        String searchString = cmdLine.optionValue(SEARCH_STRING.getOptionName());
        if (true) {

        }

        // get search output file (applied option)
        // get forcing output value (applied option)

        return result;
    }

    /**
     * Starts current Crawler instance, according to config.
     */
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
            LOG.info(String.format("Set logging level to [%s] for loggers below [%s].", logLevel, LOGGER_ROOT));
        }

        // create list of custom properties for Spring container
        List<CustomStringProperty> customProperties = SCrawler.getCustomProperties(cmdLine);

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
                for (CustomStringProperty property : customProperties) {
                    propertySources.addLast(property);
                }
            }

            // load Spring application context
            crawlerContext.refresh();

            // get SocialCrawler instance for Spring context and start it
            SCrawler crawler = (SCrawler) crawlerContext.getBean("SCrawler");
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
