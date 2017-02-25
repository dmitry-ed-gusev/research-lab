package dg.social.crawler.persistence;

import dg.social.crawler.domain.AbstractEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Abstract Hibernate Dao component, uses plain Hibernate (not JPA).
 * Created by gusevdm on 2/17/2017.
*/

@Repository // special kind of @Component (for DAO)
@Transactional
public abstract class AbstractHibernateDao<T extends AbstractEntity> {

    private static final Log LOG = LogFactory.getLog(AbstractHibernateDao.class);

    @Autowired @Qualifier("crawlerHsqlSessionFactory")
    private SessionFactory sessionFactory;
    private Class<T>       clazz;

    public AbstractHibernateDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /** Find all objects. */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        LOG.debug(String.format("Retrieving all [%s] objects.", this.clazz.getSimpleName()));
        return this.sessionFactory.getCurrentSession().createQuery("FROM " + clazz.getName()).list();
    }

    /** Find one entity by ID. */
    public T findById(Long id) {
        LOG.debug(String.format("Search object [%s] by ID = [%s].", this.clazz.getSimpleName(), id));
        return this.sessionFactory.getCurrentSession().get(clazz, id);
    }

    /** Save (persist) entity. */
    public void save(T entity) {
        LOG.debug(String.format("Saving new object [%s].", this.clazz.getSimpleName()));
        this.sessionFactory.getCurrentSession().save(entity);
    }

    /** Update entity (in persistent storage). */
    public void update(T entity) {
        LOG.debug(String.format("Updating existing object [%s].", this.clazz.getSimpleName()));
        this.sessionFactory.getCurrentSession().update(entity);
    }

    /** Save or update entity (in persistent storage) based on primary key value. */
    public void saveOrUpdate(T entity) {
        LOG.debug(String.format("Saving or updating entity [%s].", this.clazz.getSimpleName()));
        this.sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    /** Delete an existing entity object. */
    public void delete(T entity) {
        LOG.debug(String.format("Deleting existing object [%s].", this.clazz.getSimpleName()));
        this.sessionFactory.getCurrentSession().delete(entity);
    }

    /**
     * Delete an existing entity by ID.
     * @param id the id of the existing category.
     */
    public void deleteById(Long id) {
        LOG.debug(String.format("Deleting existing object [%s] by ID = [%s].", this.clazz.getSimpleName(), id));
        this.delete(this.findById(id));
    }

    /***/
    public void merge (T entity) {
        LOG.debug(String.format("Merging object [%s].", this.clazz.getSimpleName()));
        this.sessionFactory.getCurrentSession().merge(entity);
    }

    /***/
    public void persist(T entity) {
        LOG.debug(String.format("Persisting object [%s].", this.clazz.getSimpleName()));
        this.sessionFactory.getCurrentSession().persist(entity);
    }

}