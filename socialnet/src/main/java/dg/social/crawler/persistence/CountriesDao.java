package dg.social.crawler.persistence;

import dg.social.crawler.domain.CountryDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dao component for working with Country domain object.
 * Created by gusevdm on 2/17/2017.
 */

@Repository
@Transactional
public class CountriesDao extends AbstractHibernateDao <CountryDto> {

    public CountriesDao() {
        super(CountryDto.class);
    }

}
