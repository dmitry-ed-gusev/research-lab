package dg.social.crawler.persistence;

import dg.social.crawler.domain.PersonDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dao component for working with PersonDto domain object.
 * Created by gusevdm on 2/21/2017.
 */

@Repository // special kind of @Component (for DAO)
@Transactional
public class PeopleDao extends AbstractHibernateDao<PersonDto> {

    private static final Log LOG = LogFactory.getLog(PeopleDao.class);

    public PeopleDao() {
        super(PersonDto.class);
    }

}
