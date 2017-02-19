package dg.social.crawler.components;

import dg.social.crawler.domain.CountryDto;
import dg.social.crawler.persistence.CountriesDao;
import dg.social.crawler.vk.VkClient;
import dg.social.crawler.vk.VkParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    @Autowired
    private VkClient vkClient;
    @Autowired
    private CountriesDao countriesDao;

    /***/
    public void updateCountries() throws IOException, URISyntaxException, ParseException {
        LOG.debug("VkComponent.updateCountries() is working.");

        String jsonResult = this.vkClient.getCountries();
        //System.out.println("-> " + jsonResult);
        // parse search results
        List<CountryDto> countries = VkParser.parseCountries(jsonResult);
        System.out.println("countries -> " + countries);

        for (CountryDto country : countries) { // update countries in internal storage
            countriesDao.addCountry(country);
        }
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

    /***/
    public void searchByString(String searchString) {
        // todo: implement!
    }

    /***/
    public void searchExtended() {
        // todo: implement!
    }

}

