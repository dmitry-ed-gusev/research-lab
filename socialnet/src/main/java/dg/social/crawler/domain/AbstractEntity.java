package dg.social.crawler.domain;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static dg.social.crawler.CommonDefaults.SocialNetwork;

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
    static final String ID_GENERATOR_SEQUENCE = "MY_ID_SEQUENCE";

    // Entity unique persistent (DB) identifier. Used named generator (see package-info.java in current package).
    // Identifier will be generated BEFORE any insert operation to DB (that operation - real insert- may be deferred).
    @Id @Column(name = "ID")
    @GeneratedValue(generator = ID_GENERATOR_NAME)
    /*
    @GenericGenerator(
            name = "sequence11",
            strategy = "identity",//"sequence",
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "sequence",
                            value = "sequence11"
                    )
            }
    )
    @GeneratedValue (generator = "sequence11")
    */
    //@GeneratedValue//(strategy = GenerationType.AUTO)
    private long   id;

    @NotNull @Column(name = "EXTERNAL_ID", nullable = false)
    private long          externalId; // external id for different social networks

    @NotNull @Enumerated(EnumType.STRING) @Column(name = "SOCIAL_NETWORK", nullable = false)
    private SocialNetwork socialNetwork;

    @Temporal(TemporalType.TIMESTAMP) @Column(name = "CREATED_ON", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Date   createdOn;   // creation date/time, auto-assigned by Hibernate

    @Temporal(TemporalType.TIMESTAMP) @Column(name = "MODIFIED_ON")
    @org.hibernate.annotations.UpdateTimestamp
    // todo: check timestamp updating during SQL UPDATE!
    private Date   modifiedOn;   // last modification date/time, assigned by Hibernate

    @Column(name = "DELETED")
    private int    deleted = 0; // record/entity status -> 0 = active, other = deleted

    protected AbstractEntity() {}

    protected AbstractEntity(long id, long externalId, SocialNetwork socialNetwork) {
        this.id            = id;
        this.externalId    = externalId;
        this.socialNetwork = socialNetwork;
    }

    public long getId() {
       return id;
    }

    public long getExternalId() {
        return externalId;
    }

    public SocialNetwork getSocialNetwork() {
        return socialNetwork;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEntity that = (AbstractEntity) o;

        if (id != that.id) return false;
        if (externalId != that.externalId) return false;
        return socialNetwork == that.socialNetwork;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (externalId ^ (externalId >>> 32));
        result = 31 * result + socialNetwork.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("externalId", externalId)
                .append("socialNetwork", socialNetwork)
                .append("createdOn", createdOn)
                .append("modifiedOn", modifiedOn)
                .append("deleted", deleted)
                .toString();
    }

}