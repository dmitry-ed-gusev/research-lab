package dg.social.crawler.persistence;

import dg.social.crawler.domain.PersonDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Dao component for working with PersonDto domain object.
 * Created by gusevdm on 2/21/2017.
 */

@Repository // special kind of @Component (for DAO)
@Transactional
public class PeopleDao extends AbstractHibernateDao<PersonDto> {

    private static final Log LOG = LogFactory.getLog(PeopleDao.class);

    private static final int FLUSH_COUNTER = 30;
    private static final int PERCENT_REPORT_COUNTER = 10;

    public PeopleDao() {
        super(PersonDto.class);
    }

    /***/
    public void addOrUpdatePerson(PersonDto person) {
        //LOG.debug(String.format("PeopleDao.addOrUpdatePerson() is working. PersonDto instance:%n%s", person)); // <- too much output

        if (person == null) { // fail-fast
            throw new IllegalArgumentException("Can't add/update null Person!");
        }

        Session session = this.getSessionFactory().getCurrentSession();
        String hql      = "from PersonDto as p where p.externalId = :externalId and p.socialNetwork = :socialNetwork";
        PersonDto foundPerson = (PersonDto) session.createQuery(hql)
                .setString("externalId", String.valueOf(person.getExternalId()))
                .setString("socialNetwork", person.getSocialNetwork().name())
                .uniqueResult();

        if (foundPerson == null) { // not fount - saving
            //LOG.debug(String.format("Person not found. Saving.%n%s", person)); // <- too much output
            this.save(person);
        } else { // found - already exists - updating
            //LOG.debug(String.format("Person found. Updating.%n%s", person)); // <- too much output
            person.setId(foundPerson.getId());
            this.merge(person);
        }

    }

    /***/
    public void loadPeople(List<PersonDto> people) {
        LOG.debug(String.format("PeopleDao.loadPeople() is working. List size [%s].", people == null ? 0 : people.size()));

        if (people == null) {
            throw new IllegalArgumentException("Can't load data from null list!");
        }

        final int reportCounter = people.size() / PERCENT_REPORT_COUNTER;
        int counter = 1;
        Session session = this.getSessionFactory().getCurrentSession();
        // iterate through list and
        for (PersonDto person : people) {
            this.addOrUpdatePerson(person);

            if (counter % FLUSH_COUNTER == 0) { // flush and clear session
                session.flush();
                session.clear();
            }

            if (counter % reportCounter == 0) { // report (debug output)
                LOG.debug(String.format("Processed records [%s/%s].", counter, people.size()));
            }

            counter++;
        } // end of FOR

        session.flush();
        session.clear();
        LOG.debug("Loading of people list is finished.");
    }

}
