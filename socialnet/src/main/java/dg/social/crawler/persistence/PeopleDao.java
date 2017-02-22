package dg.social.crawler.persistence;

import dg.social.crawler.domain.PersonDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
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

    public void addOrUpdatePerson(PersonDto person) {
        LOG.debug(String.format("PeopleDao.addOrUpdate() is working. PersonDto instance:%n%s", person));

        if (person == null) { // fail-fast
            throw new IllegalArgumentException("Can't add/update null Person!");
        }

        Session session = this.getSessionFactory().getCurrentSession();
        String hql      = "from PersonDto as p where p.externalId = :externalId and " +
                "p.socialNetwork = :socialNetwork";
        PersonDto foundPerson = (PersonDto) session.createQuery(hql)
                .setString("externalId", String.valueOf(person.getExternalId()))
                .setString("socialNetwork", person.getSocialNetwork().name())
                .uniqueResult();

        if (foundPerson == null) { // not fount - saving
            LOG.debug(String.format("Person not found. Saving.%n%s", person));
            this.save(person);
        } else { // found - already exists - updating
            LOG.debug(String.format("Person found. Updating.%n%s", person));
            // todo: implement updating!
        }

    }
}
