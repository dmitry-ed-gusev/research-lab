package dg.social.crawler.components;

import dg.social.crawler.SCrawlerDefaults;
import dg.social.crawler.domain.CountryDto;
import dg.social.crawler.domain.PersonDto;
import dg.social.crawler.persistence.CountriesDao;
import dg.social.crawler.networks.vk.VkClient;
import dg.social.crawler.networks.vk.VkParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * VK social network component.
 * Created by vinnypuhh on 19.02.17.
 */

@Component
@Transactional
public class VkComponent {

    private static final Log LOG = LogFactory.getLog(VkComponent.class);

    private static final String SEARCH_FIELDS_LIST =
            "about,activities,bdate,books,career,city,contacts,country,education,exports,games, home_town," +
            "interests,home_town,maiden_name,movies,music,nickname,occupation,personal,quotes, " +
            "relatives,relation,schools,sex,site,status,tv,universities";
    private static final int    SEARCH_RESULTS_COUNT = 1000;

    @Autowired
    private VkClient vkClient;
    @Autowired
    private CountriesDao countriesDao;

    /***/
    public void updateCountries() throws IOException, URISyntaxException, ParseException {
        LOG.debug("VkComponent.updateCountries() is working.");

        //String jsonResult = this.vkClient.getCountries();
        //System.out.println("-> " + jsonResult);
        // parse search results
        //List<CountryDto> countries = VkParser.parseCountries(jsonResult);
        //System.out.println("countries -> " + countries);

        Session session = this.countriesDao.getSessionFactory().getCurrentSession();
        //for (CountryDto country : countries) { // update countries in internal storage
            //cou1ntriesDao.addCountry(country);

            //System.out.println("\n1 --> " + country);
            //System.out.println("*** " + session.merge(country));
            //System.out.println("2 --> " + country + "\n");

            //session.flush();
            //session.clear();

        //}

        CountryDto ccc = new CountryDto(-8, 75, "ZtrgdfbdxZxZx", SCrawlerDefaults.SocialNetwork.VK);
        System.out.println("---> " + ccc);
        System.out.println("ffff " + session.merge(ccc));
        //session.save(ccc);
        session.flush();
        session.clear();

        //session.createQuery("CHECKPOINT").executeUpdate();
        //session.co createQuery("SHUTDOWN").executeUpdate();
        //this.countriesDao.getSessionFactory().close();

    }

    /***/
    public void updateCities() {
        // todo: implement!
    }

    /***/
    public void updateUniversities() {
        // todo: implement!
    }

    /***/
    public void updateFaculties() {
        // todo: implement!
    }

    /***/
    public void outputToFile(String fileName) {
        // todo: implement!
    }

    /**
     * Simple search by search string.
     */
    public List<PersonDto> searchByString(String searchString) throws IOException, URISyntaxException, ParseException {
        LOG.debug(String.format("VkComponent.searchByString() is working. Search string:%n%s", searchString));

        if (StringUtils.isBlank(searchString)) { // fail-fast
            throw new IllegalArgumentException("Can't search by empty string!");
        }

        // do the search, parse results and return list of found people
        String jsonResult = this.vkClient.usersSearch(searchString, SEARCH_FIELDS_LIST , SEARCH_RESULTS_COUNT);

        return VkParser.parseUsers(jsonResult);
    }

    /***/
    public void searchExtended() {
        // todo: implement!
    }

}

