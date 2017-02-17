package dg.social.crawler.domain;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Base abstract domain object with database identity. Incapsulates some common (for all domain objects)
 * properties. Domain entity objects (not value objects!) should extend this object.
 * Used Hibernate mapping strategy "Table per concrete class with polymorphism".
 * Created by gusevdm on 2/17/2017.
*/

@Entity
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    static final String ID_GENERATOR_NAME     = "ID_GENERATOR";
    static final String ID_GENERATOR_SEQUENCE = "ID_SEQUENCE";

    // Entity unique persistent (DB) identifier. Used named generator (see package-info.java in current package).
    // Identifier will be generated BEFORE any insert operation to DB (that operation - real insert- may be deferred).
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = ID_GENERATOR_NAME)
    private long   id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_ON", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Date   createdOn;   // creation date/time, auto-assigned by Hibernate

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFIED_ON")
    @org.hibernate.annotations.UpdateTimestamp
    // todo: check timestamp updating during SQL UPDATE
    private Date   modifiedOn;   // last modification date/time, assigned by Hibernate

    @Column(name = "DELETED")
    private int    deleted = 0; // record/entity status -> 0 = active, other = deleted

    protected AbstractEntity() {}

    protected AbstractEntity(long id) {
        this.id = id;
    }

    public long getId() {
       return id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public int getDeleted() {
        return deleted;
    }

    @Override
    @SuppressWarnings("MethodWithMultipleReturnPoints")
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        // comparing
        AbstractEntity other = (AbstractEntity) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("createdOn", createdOn)
                .append("modifiedOn", modifiedOn)
                .append("deleted", deleted)
                .toString();
    }

}