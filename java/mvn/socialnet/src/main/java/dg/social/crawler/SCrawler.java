package dg.social.crawler;

import dg.social.crawler.components.TelescopeComponent;
import dg.social.crawler.components.VkComponent;
import dg.social.crawler.utilities.CmdLineOption;
import dg.social.crawler.utilities.CommonUtilities;
import dgusev.spring.CustomStringProperty;
import dgusev.cmd.CmdLine;
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
import java.util.Arrays;
import java.util.List;

import static dg.social.crawler.utilities.CmdLineOption.LOGGER_LEVEL;

/**
 * Crawler for social dg.social.crawler.networks. This class uses social dg.social.crawler.networks clients for mining info
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

    // session factory for working with persistance layer
    @Autowired @Qualifier(value = "crawlerHsqlSessionFactory")
    private SessionFactory     sessionFactory;

    // crawler configuration
    @Autowired private SCrawlerConfig     crawlerСonfig;
    // VK component
    @Autowired private VkComponent        vkComponent;
    // Telescope component
    @Autowired private TelescopeComponent tsComponent;

    /***/
    public SCrawler() {
    }

    /**
     * Starts current Crawler instance, according to the given config.
     */
    public void start() throws ParseException, IOException, URISyntaxException {
        LOG.debug("SocialCrawler.start() is working.");

        LOG.debug(String.format("\n==========\nSocialCrawler config:\n%s\n==========", this.crawlerСonfig));

        //this.vkComponent.updateCountries();

        // Telescope -> load data from file
        if (!StringUtils.isBlank(this.crawlerСonfig.getTelescopeCsv())) {
            LOG.debug(String.format("Loading data from Telescope export file [%s].", this.crawlerСonfig.getTelescopeCsv()));
            this.tsComponent.loadTelescopeData(this.crawlerСonfig.getTelescopeCsv());
        }

        // VK -> perform quick search
        if (!StringUtils.isBlank(this.crawlerСonfig.getSearchString())) {
            LOG.debug(String.format("Performing quick search on VK network. Search string [%s].",
                    this.crawlerСonfig.getSearchString()));
            this.vkComponent.searchByString(this.crawlerСonfig.getSearchString());
        }

        // shutting Crawler down
        LOG.info("Finishing SocialCrawler...");
        this.sessionFactory.getCurrentSession().createSQLQuery("CHECKPOINT").executeUpdate();
        this.sessionFactory.getCurrentSession().createSQLQuery("SHUTDOWN").executeUpdate();
    }

    /***/
    public static void main(String[] args) {
        LOG.info(String.format("SocialCrawler starting. Command line: [%s].", Arrays.toString(args)));

        // init CmdLine instance
        CmdLine cmdLine = new CmdLine(args); // parse cmd line and disable internal logging

        // if there is -help option -> show usage text and exit
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
        List<CustomStringProperty> customProperties = CommonUtilities.getCustomPropertiesList(cmdLine);

        try {

            // initialize Spring application context
            AbstractApplicationContext crawlerContext = new ClassPathXmlApplicationContext(
                    new String[] {SPRING_CONFIG}, false);
            // apply custom properties (put them into Spring context)
            if (!customProperties.isEmpty()) {
                LOG.debug("There are custom properties! Putting them into Spring context.");
                MutablePropertySources propertySources = crawlerContext.getEnvironment().getPropertySources();
                for (CustomStringProperty property : customProperties) {
                    propertySources.addLast(property);
                }
            }

            // load Spring application context (after putting custom properties)
            crawlerContext.refresh();

            // get SocialCrawler instance for Spring context and start it
            SCrawler crawler = (SCrawler) crawlerContext.getBean("SCrawler");
            crawler.start(); // start Crawler

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
