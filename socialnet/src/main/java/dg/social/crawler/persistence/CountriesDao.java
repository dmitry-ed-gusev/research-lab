package dg.social.crawler.persistence;

import dg.social.crawler.domain.CountryDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dao component for working with CountryDto domain object.
 * Created by gusevdm on 2/17/2017.
 */

@Repository // special kind of @Component (for DAO)
@Transactional
public class CountriesDao extends AbstractHibernateDao <CountryDto> {

    private static final Log LOG = LogFactory.getLog(CountriesDao.class);

    public CountriesDao() {
        super(CountryDto.class);
    }

    /***/
    public void addCountry(CountryDto country) {

        if (country == null) { //fail-fast
            throw new IllegalArgumentException("Can't add/update null country!");
        }

        // try to find existing country
        Session    session = this.getSessionFactory().getCurrentSession();
        String     hql     = "from CountryDto as c where c.externalId = :externalId and " +
                "c.countryName = :countryName and c.networkType = :networkType";
        CountryDto tmpCountry = (CountryDto) session.createQuery(hql)
                .setString("externalId",  String.valueOf(country.getExternalId()))
                .setString("countryName", country.getCountryName())
                .setString("networkType", String.valueOf(country.getSocialNetwork()))
                .uniqueResult();

        if (tmpCountry == null) { // not found - saving
            LOG.debug(String.format("Country [%s] not found. Saving.", country));
            this.save(country);
        } else {
            LOG.debug(String.format("Country [%s] already exists.", country));
        }

    }

}
